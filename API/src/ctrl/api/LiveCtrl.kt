package ctrl.api

import ctrl.common.Auth
import ctrl.common.Author
import ctrl.common.BadRequestException
import data.LiveEntity
import lib.log.log
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.DigestUtils
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/live")
private class LiveCtrl {
    /** 依赖注入：数据库会话工厂 */
    @Autowired val factory: SessionFactory? = null
    @Autowired val author: Author? = null

    data class LiveRequestBody(val time: String, val category: String, val title: String, val auth: Auth)

    /**
     * API: 用户发起直播预订请求
     */
    @PostMapping("order")
    private fun order(@RequestBody json: LiveRequestBody): Any? {
        author!!.auth(json.auth)
        factory!!.openSession()!!.use { session ->
            // 对请求进行md5签名
            val md5 = DigestUtils.md5DigestAsHex("${ json.title }${ json.auth }${ System.nanoTime() }".toByteArray())
            session.beginTransaction()
            session.createNativeQuery(
                    "insert into live(uid, md5, title, category, order_time, live_time) " +
                            "value(:uid, :md5, :title, :category, now(), :live_time)")
                    .setParameter("uid", json.auth.uid)
                    .setParameter("md5", md5)
                    .setParameter("title", json.title)
                    .setParameter("category", json.category)
                    .setParameter("live_time", json.time)
                    .executeUpdate()
            session.transaction.commit()
            return session.createNativeQuery(
                    "select `index`, md5 from live where md5=:md5")
                    .setParameter("md5", md5)
                    .setMaxResults(1)
                    .uniqueResult()!!.apply {
                        this as Array<*>
                        if (size != 2) throw BadRequestException("预订失败")
                    }
        }
    }

    /**
     * nginx publish 事件
     * live状态由pending、awaiting转为living
     */
    @GetMapping("publish")
    private fun onPublish(@RequestParam name: String, @RequestParam md5: String) {
        factory!!.openSession()!!.use { session ->
            session.beginTransaction()
            val rs = session.createNativeQuery(
                    "update live set status='living' where `index`=:name and md5=:md5 and status!='done'")
                    .setParameter("name", name)
                    .setParameter("md5", md5)
                    .executeUpdate()
            session.transaction.commit()
            if (rs < 1) throw BadRequestException("Permission denied")
        }
    }

    /**
     * nginx publish_done 事件
     * live状态由living转为awaiting
     */
    @GetMapping("publish_done")
    private fun onPublishDone(@RequestParam name: String, @RequestParam md5: String) {
        log.d("完成直播")
        factory!!.openSession()!!.use { session ->
            session.beginTransaction()
            val rs = session.createNativeQuery(
                    "update live set status='awaiting' where `index`=:name and md5=:md5 and status!='done'")
                    .setParameter("name", name)
                    .setParameter("md5", md5)
                    .executeUpdate()
            session.transaction.commit()
            if (rs < 1) throw BadRequestException("Permission denied")
        }
    }

    data class DoneJSON(val auth: Auth)
    /**
     * API: 播主主动结束直播
     * live状态转为done
     */
    @PostMapping("done")
    private fun done(@RequestBody json: DoneJSON) {
        factory!!.openSession()!!.use { session ->
            session.beginTransaction()
            val rs = session.createNativeQuery(
                    "update live set status='done' where uid=:uid and status!='done'")
                    .setParameter("uid", json.auth.uid)
                    .executeUpdate()
            session.transaction.commit()
            if (rs < 1) throw BadRequestException("Permission denied")
        }
    }

    /**
     * API: 访问用户直播间
     */
    @GetMapping("visit/{uid:\\d+}")
    private fun visit(@PathVariable uid: Int): Any? {
        factory!!.openSession()!!.use { session ->
            val rs = session.createNativeQuery(
                    "select * from live where uid=:uid order by order_time desc", LiveEntity::class.java)
            .setParameter("uid", uid)
                    .setMaxResults(1)
                    .resultList.let {
                        if (it.size < 1) throw BadRequestException("直播间不存在，或没有直播计划")
                        it as List<*>
                    }
            return rs[0]
        }
    }

    /**
     * API: 查询公开直播
     */
    @GetMapping("search/{category}/{keyword}")
    private fun search(@PathVariable category: String, @PathVariable keyword: String): Any {
        log.d(category)
        log.d(keyword)
        factory!!.openSession()!!.use {  session ->
            return session.createNativeQuery(
                    ("select live.uid, title, category, live_time, nickname, status from live, user " +
                            "where status!='done' and title like :keyword and live.uid=user.uid " +
                            if (category == "全部分类") "" else "and category=:category").apply { log.d("cmd", this) })
                    .setParameter("keyword", "%${ keyword }%")
                    .apply {
                        if (category != "全部分类")
                            setParameter("category", category)
                    }
                    .resultList
        }
    }
}