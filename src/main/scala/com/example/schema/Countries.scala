package com.example.schema
import slick.jdbc.H2Profile.api._

class Countries(tag: Tag) extends Table[(String, String, String)](tag, "country") {
  def code = column[String]("code", O.PrimaryKey)
  def name = column[String]("name")
  def continent = column[String]("continent")
  def * = (code, name, continent)
}
