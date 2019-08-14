package cn.edu.gzmu.authorization.util

import cn.edu.gzmu.authorization.model.constant.SQL_ALL
import io.vertx.core.json.JsonObject

/**
 * ext
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/14 下午10:46
 */
fun JsonObject.getCondition(key: String): String {
  var condition: String = getString(key, SQL_ALL)
  if(condition !== SQL_ALL)
    condition = SQL_ALL + condition + SQL_ALL
  return condition
}
