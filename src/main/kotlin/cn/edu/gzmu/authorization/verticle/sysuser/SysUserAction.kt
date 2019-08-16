package cn.edu.gzmu.authorization.verticle.sysuser

/**
 * actions
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/14 下午1:52
 */
// ---------------------------- path
const val ALL = "/user/all"

// ---------------------------- sql
const val QUERY_PAGE =
  """SELECT *
  FROM sys_user
  WHERE is_enable = TRUE
      and name like ?
     and phone like ?
     and email like ?
  order by id
  limit ?,?
  """
const val QUERY_COUNT = """
  SELECT COUNT(*) FROM sys_user
  WHERE is_enable = TRUE
      and name like ?
     and phone like ?
     and email like ?
"""

// ---------------------------- condition
const val NAME = "name"
const val PHONE = "phone"
const val EMAIL = "email"
