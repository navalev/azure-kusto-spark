package com.microsoft.kusto.spark.utils

import com.microsoft.kusto.spark.datasink.KustoWriter.TempIngestionTablePrefix

object CslCommandsGenerator{

  // Not used. Here in case we prefer this approach
  def generateFindOldTemporaryTablesCommand2(database: String): String = {
    s""".show database $database extents metadata | where TableName startswith '$TempIngestionTablePrefix' | project TableName, maxDate = todynamic(ExtentMetadata).MaxDateTime | where maxDate > ago(1h)"""
  }

  def generateFindOldTempTablesCommand(database: String): String = {
    s""".show journal | where Event == 'CREATE-TABLE' | where Database == '$database' | where EntityName startswith '$TempIngestionTablePrefix' | where EventTimestamp < ago(1h) and EventTimestamp > ago(3d) | project EntityName """
  }

  def generateFindCurrentTempTablesCommand(prefix: String): String = {
    s""".show tables | where TableName startswith '$prefix' | project TableName """
  }

  def generateDropTablesCommand(tables: String): String = {
    s".drop tables ($tables) ifexists"
  }

  // Table name must be normalized
  def generateTableCreateCommand(tableName: String, columnsTypesAndNames: String): String = {
    s".create table $tableName ($columnsTypesAndNames)"
  }

  def generateTableShowSchemaCommand(table: String): String = {
    s".show table $table schema as json"
  }

  def generateTableDropCommand(table: String): String = {
    s".drop table $table ifexists"
  }

  def generateCreateTmpStorageCommand(): String = {
    s".create tempstorage"
  }

  def generateTableMoveExtentsCommand(sourceTableName:String, destinationTableName: String): String ={
    s".move extents all from table $sourceTableName to table $destinationTableName"
  }

  def generateTableAlterMergePolicyCommand(table: String, allowMerge: Boolean, allowRebuild: Boolean): String ={
    s""".alter table $table policy merge @'{"AllowMerge":"$allowMerge", "AllowRebuild":"$allowRebuild"}'"""
  }
}
