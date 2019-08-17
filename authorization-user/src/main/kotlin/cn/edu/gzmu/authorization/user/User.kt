package cn.edu.gzmu.authorization.user

import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get
import java.time.LocalDateTime

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/17 上午10:34
 */
@DataObject(generateConverter = true)
data class User(
  var id: Long = 0,
  var name: String? = null,
  var spell: String? = null,
  var pwd: String? = null,
  var status: Int? = null,
  var icon: String? = null,
  var email: String? = null,
  var phone: String? = null,
  var onlineStatus: Boolean = false,
  var sort: Short = 1,
  var createUser: String? = null,
  var createTime: LocalDateTime = LocalDateTime.now(),
  var modifyUser: String? = null,
  var modifyTime: LocalDateTime = LocalDateTime.now(),
  var remark: String = "",
  var isEnable: Boolean = true
) {

  fun toJson(): JsonObject =
    JsonObject().put("id", id).put("name", name)
      .put("spell", spell).put("pwd", pwd)
      .put("status", status).put("icon", icon)
      .put("email", email).put("phone", phone)
      .put("online_status", onlineStatus).put("sort", sort)
      .put("create_user", createUser).put("create_time", createTime)
      .put("modify_user", modifyUser).put("modify_time", modifyTime)
      .put("remark", remark).put("isEnable", isEnable)

  constructor(json: JsonObject) : this() {
    this.id = json.getLong("id", 0)!!
    this.spell = json.getString("spell")
    this.pwd = json.getString("pwd")
    this.status = json.getInteger("status")
    this.icon = json.getString("icon")
    this.email = json.getString("email")
    this.phone = json.getString("phone")
    this.onlineStatus = json.getBoolean("online_status")
    this.sort = json["sort"]
    this.createUser = json["create_user"]
    this.createTime = json["create_time"]
    this.modifyUser = json["modify_user"]
    this.modifyTime = json["modify_time"]
    this.remark = json["remark"]
    this.isEnable = json["isEnable"]
  }
}
