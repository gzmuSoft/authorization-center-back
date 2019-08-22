package cn.edu.gzmu.authorization.data

import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get
import java.time.LocalDateTime

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/22 下午1:07
 */
@DataObject(generateConverter = true)
data class Data(
  var id: Long = 0,
  var name: String? = null,
  var spell: String? = null,
  var parentId: Long = 0,
  var brief: String? = null,
  var type: Int? = 0,
  var sort: Short = 1,
  var createUser: String? = null,
  var createTime: LocalDateTime = LocalDateTime.now(),
  var modifyUser: String? = null,
  var modifyTime: LocalDateTime = LocalDateTime.now(),
  var remark: String? = null,
  var isEnable: Boolean = true
) {

  fun toJson(): JsonObject =
    JsonObject().put("id", id).put("name", name)
      .put("spell", spell).put("parentId", parentId)
      .put("brief", brief).put("type", type).put("sort", sort)
      .put("create_user", createUser).put("create_time", createTime)
      .put("modify_user", modifyUser).put("modify_time", modifyTime)
      .put("remark", remark).put("isEnable", isEnable)

  constructor(json: JsonObject) : this() {
    this.id = json.getLong("id", 0)!!
    this.name = json.getString("name")
    this.spell = json.getString("spell")
    this.parentId = json.getLong("parentId")
    this.brief = json.getString("brief")
    this.type = json.getInteger("type")
    this.sort = json["sort"]
    this.createUser = json["create_user"]
    this.createTime = json["create_time"]
    this.modifyUser = json["modify_user"]
    this.modifyTime = json["modify_time"]
    this.remark = json["remark"]
    this.isEnable = json["isEnable"]
  }
}
