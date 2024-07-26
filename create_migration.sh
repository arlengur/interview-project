#!/bin/sh
exec scala "$0" "$@"
!#
object Main {
  def main(args: Array[String]): Unit = {
    if(args.length < 1) {
      println("Usage: create_migration.sh <migration_name>")
      System.exit(-1)
    }
    val name = args(0)
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = new java.util.Date();
    val ts = date.getTime / 1000
    val projectPath = "."
    val migrationPath = s"$projectPath/src/main/resources/db/migration"
    val filePath = s"${migrationPath}/V${ts}__${name}.sql"
    
    val dateStr = format.format(date)

    val out = s"""
        |-- Migration: ${name}
        |-- Created at: ${dateStr}
        |
        |BEGIN;
        |
        |
        |
        |
        |COMMIT;
    """.stripMargin

    val outFile = new java.io.FileOutputStream(filePath)
    outFile.write(out.getBytes)
    outFile.close()
    println(s"file created $filePath")
  }
}

