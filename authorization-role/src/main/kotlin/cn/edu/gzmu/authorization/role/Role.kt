package cn.edu.gzmu.authorization.role

import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import java.time.LocalDateTime

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/21 下午2:16
 */
@DataObject(generateConverter = true)
data class Role(
  var id: Long = 0,
  var name: String? = null,
  var spell: String? = null,
  var des: String? = null,
  var iconCls: String? = null,
  var parentId: Long = 0,
  var sort: Int = 1,
  var createUser: String? = null,
  var createTime: LocalDateTime = LocalDateTime.now(),
  var modifyUser: String? = null,
  var modifyTime: LocalDateTime = LocalDateTime.now(),
  var remark: String? = null,
  var isEnable: Boolean = true
) {

  fun toJson(): JsonObject =
    json {
      obj(
        "id" to id, "name" to name, "spell" to spell,
        "des" to des, "icon_cls" to iconCls, "parent_id" to parentId,
        "sort" to sort, "create_user" to createUser, "create_time" to createTime,
        "modify_user" to modifyUser, "modify_time" to modifyTime,
        "remark" to remark, "is_enable" to isEnable
      )
    }

  constructor(json: JsonObject) : this() {
    this.id = json.getLong("id", 0)!!
    this.name = json.getString("name")
    this.spell = json.getString("spell")
    this.des = json.getString("des")
    this.iconCls = json.getString("icon_cls")
    this.parentId = json.getLong("parent_id", 0)
    this.sort = json["sort"]
    this.createUser = json["create_user"]
    this.createTime = json["create_time"]
    this.modifyUser = json["modify_user"]
    this.modifyTime = json["modify_time"]
    this.remark = json["remark"]
    this.isEnable = json["isEnable"]
  }
}
