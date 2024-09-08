package com.atguigu.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Connection : 通过ConnectionFactory获取. 是重量级实现.
 * Table : 主要负责DML操作
 * Admin : 主要负责DDL操作
 */
public class HBaseDemo {

    private static Connection connection ;

    static{
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum","hadoop102,hadoop103,hadoop104");
        try {
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        createNameSpace("mydb2");

        //createTable("","t1","info1","info2");

        //dropTable("","t1");

        //putData("","stu","1003","info","name","wangwu");

        //deleteData("","stu","1003","info","name");

        //getData("","stu","1001","info","name");

        //scanData("","stu","1001","1003");

        createTableWithRegions("","staff4","info");
    }

    public static void createTableWithRegions(String nameSpaceName, String tableName,String ... cfs ) throws IOException {

        if(existsTable(nameSpaceName, tableName)){
            System.err.println((nameSpaceName == null || nameSpaceName.equals("")? "default" : nameSpaceName)  + ":" + tableName  + "表已经存在");
            return ;
        }

        Admin admin = connection.getAdmin() ;

        TableDescriptorBuilder tableDescriptorBuilder =
                TableDescriptorBuilder.newBuilder(TableName.valueOf(nameSpaceName,tableName));

        if(cfs == null || cfs.length < 1){
            System.err.println("至少指定一个列族");
            return ;
        }

        for (String cf : cfs) {

            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder =
                    ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf));
            ColumnFamilyDescriptor columnFamilyDescriptor =
                    columnFamilyDescriptorBuilder.build();

            tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptor);
        }

        TableDescriptor tableDescriptor = tableDescriptorBuilder.build();

        byte [][] splitkeys = new byte[4][];

        //['1000','2000','3000','4000']
        splitkeys[0] = Bytes.toBytes("1000");
        splitkeys[1] = Bytes.toBytes("2000");
        splitkeys[2] = Bytes.toBytes("3000");
        splitkeys[3] = Bytes.toBytes("4000");

        admin.createTable(tableDescriptor,splitkeys);

        admin.close();
    }

    /**
     * scan
     */
    public static void scanData(String nameSpaceName,String tableName,String startRow, String stopRow) throws IOException {
        Table table  = connection.getTable(TableName.valueOf(nameSpaceName,tableName));
        Scan scan = new Scan();
        //scan.withStartRow(Bytes.toBytes(startRow));
        //scan.withStopRow(Bytes.toBytes(stopRow));
        scan.withStartRow(Bytes.toBytes(startRow)).withStopRow(Bytes.toBytes(stopRow));

        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                String cellString = Bytes.toString(CellUtil.cloneRow(cell))  + " : " +
                        Bytes.toString(CellUtil.cloneFamily(cell)) + " : " +
                        Bytes.toString(CellUtil.cloneQualifier(cell))+ " : " +
                        Bytes.toString(CellUtil.cloneValue(cell));

                System.out.println(cellString);
            }
            System.out.println("-----------------------------------------------");
        }

        table.close();
    }


    /**
     * get
     */
    public static void getData(String nameSpaceName,String tableName,String rowkey,String cf, String cl) throws IOException {
        Table table  = connection.getTable(TableName.valueOf(nameSpaceName,tableName));
        Get get = new Get(Bytes.toBytes(rowkey));

        //get.addFamily(Bytes.toBytes(cf));

        get.addColumn(Bytes.toBytes(cf),Bytes.toBytes(cl));

        Result result = table.get(get);
        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            String cellString = Bytes.toString(CellUtil.cloneRow(cell))  + " : " +
                    Bytes.toString(CellUtil.cloneFamily(cell)) + " : " +
                    Bytes.toString(CellUtil.cloneQualifier(cell))+ " : " +
                    Bytes.toString(CellUtil.cloneValue(cell));

            System.out.println(cellString);
        }

        table.close();
    }



    /**
     * delete
     */
    public static void deleteData(String nameSpaceName,String tableName,String rowkey,String cf, String cl) throws IOException {
        Table table  = connection.getTable(TableName.valueOf(nameSpaceName,tableName));

        Delete delete = new Delete(Bytes.toBytes(rowkey));  //如果只指定rowkey,就是删除整条数据

        //delete.addFamily(Bytes.toBytes(cf)); 指定删除某个列族的数据  DeleteFamily

        delete.addColumns(Bytes.toBytes(cf),Bytes.toBytes(cl));  // DeleteColumn

        //delete.addColumn(Bytes.toBytes(cf),Bytes.toBytes(cl)); // Delete

        table.delete(delete);

        table.close();
    }


    /**
     * put
     */
    public static void putData(String nameSpaceName,String tableName,String rowkey,String cf, String cl, String value ) throws IOException {
        Table table  = connection.getTable(TableName.valueOf(nameSpaceName,tableName));

        Put put = new Put(Bytes.toBytes(rowkey));

        put.addColumn(Bytes.toBytes(cf),Bytes.toBytes(cl),Bytes.toBytes(value));

        table.put(put);

        table.close();
    }


    /**
     * 删除表
     */
    public static void dropTable(String nameSpaceName,String tableName) throws IOException {
        if(!existsTable(nameSpaceName, tableName)){
            System.err.println("表不存在");
            return ;
        }

        Admin admin = connection .getAdmin();
        TableName tn = TableName.valueOf(nameSpaceName, tableName);
        admin.disableTable(tn);
        admin.deleteTable(tn);
        admin.close();
    }


    /**
     * 判断表是否存在
     */
    public static boolean   existsTable(String nameSpaceName,String tableName) throws IOException {
        Admin admin =  connection.getAdmin();
        return admin.tableExists(TableName.valueOf(nameSpaceName, tableName)) ;
    }


    /**
     * 创建table
     */
    public static void createTable(String nameSpaceName, String tableName,String ... cfs ) throws IOException {

        if(existsTable(nameSpaceName, tableName)){
            System.err.println((nameSpaceName == null || nameSpaceName.equals("")? "default" : nameSpaceName)  + ":" + tableName  + "表已经存在");
            return ;
        }

        Admin admin = connection.getAdmin() ;

        TableDescriptorBuilder tableDescriptorBuilder =
                TableDescriptorBuilder.newBuilder(TableName.valueOf(nameSpaceName,tableName));

        if(cfs == null || cfs.length < 1){
            System.err.println("至少指定一个列族");
            return ;
        }

        for (String cf : cfs) {

            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder =
                    ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf));
            ColumnFamilyDescriptor columnFamilyDescriptor =
                    columnFamilyDescriptorBuilder.build();

            tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptor);
        }

        TableDescriptor tableDescriptor = tableDescriptorBuilder.build();


        admin.createTable(tableDescriptor);

        admin.close();
    }



    /**
     * 创建NameSpace
     */
    public static void createNameSpace(String nameSpace) throws IOException {
        // 基本的判空操作
        if(nameSpace == null || nameSpace.equals("")){
            System.err.println("nameSpace名字不能为空");
            return ;
        }
        // 获取Admin对象
        Admin admin = connection.getAdmin();
        NamespaceDescriptor.Builder builder = NamespaceDescriptor.create(nameSpace);
        NamespaceDescriptor namespaceDescriptor = builder.build();
        try {
            // 调用方法
            admin.createNamespace(namespaceDescriptor);
            System.out.println(nameSpace + " 创建成功");
        }catch (NamespaceExistException e){
            System.err.println(nameSpace + " 已经存在") ;
        }finally{
            admin.close();
        }
    }

}
