package ctrl.api

import ctrl.common.Auth
import ctrl.common.Author
import ctrl.common.BadRequestException
import lib.log.log
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
@RequestMapping("/api/resource")
private class ResourceCtrl {
    /** 依赖注入：数据库会话工厂 */
    @Autowired val factory: SessionFactory? = null
    /** 依赖注入：认证器 */
    @Autowired val author: Author? = null
    /** 上传目录路径，必须是绝对路径 */
    val uploadDir: String = File("./webapps/res").let {
        if (!it.exists()) it.mkdirs()
        return@let it.canonicalPath
    }.apply { log.d("用户上传目录：", this) }

    /**
     * API: 用户上传资源
     */
    @PostMapping("upload")
    private fun liveRequest(@RequestPart file: MultipartFile, @RequestPart auth: Auth): Any {
        author!!.auth(auth)
        log.d("用户上传文件", file.originalFilename)
        val filename = file.originalFilename?: ""
        var name = filename
        var ext = ""
        """(.+)\.([\d\w]+)""".toRegex().matchEntire(filename)?.destructured?.let {
            name = it.component1()
            ext = it.component2()
        }
        var i = 0
        val toDir = File("${ uploadDir }/${ auth.uid }").apply { this.mkdirs() }
        var to = File("${ toDir }/${ filename }")
        while (to.exists())
            to = File("${ toDir }/${ name }(${ ++i })${ if (ext == "") "" else ".${ ext }" }")
        file.transferTo(to)
        log.d(file.originalFilename, "保存至", to)
        factory!!.openSession()!!.use { session ->
            session.beginTransaction()
            session.createNativeQuery(
                    "insert into assets(uid, path, filename, name, ext, type, upload_time, size) " +
                            "value(:uid, :path, :filename, :name, :ext, :type, now(), :size)")
                    .setParameter("uid", auth.uid)
                    .setParameter("path", "/res${ to.canonicalPath.replace(uploadDir, "") }")
                    .setParameter("filename", filename)
                    .setParameter("name", name)
                    .setParameter("ext", ext)
                    .setParameter("type", file.contentType)
                    .setParameter("size", file.size)
                    .executeUpdate()
            session.transaction.commit()
        }
        return Unit
    }

    private data class QueryMineBody(val auth: Auth)

    /**
     * API: 搜索用户资源
     */
    @PostMapping("mine")
    private fun queryMine(@RequestBody json: QueryMineBody): Any {
        author!!.auth(json.auth)
        factory!!.openSession()!!.use {  session ->
            return session.createNativeQuery(
                    "select name, ext, type, size, upload_time, path, status, `index` from assets " +
                            "where uid=:uid and status!=-1")
                    .setParameter("uid", json.auth.uid)
                    .resultList
        }
    }

    private data class ResStatusBody(val index: Int, val status: Int, val auth: Auth)

    /**
     * API: 改变资源状态：公开(0)或私有(1)、删除(-1)
     */
    @PostMapping("status")
    private fun resStatus(@RequestBody json: ResStatusBody) {
        author!!.auth(json.auth)
        factory!!.openSession()!!.use {  session ->
            session.beginTransaction()
            val rs = session.createNativeQuery(
                    "update assets set status=:status " +
                            "where `index`=:index and uid=:uid and status!=-1")
                    .setParameter("index", json.index)
                    .setParameter("status", json.status)
                    .setParameter("uid", json.auth.uid)
                    .executeUpdate()
            if (rs != 1) throw BadRequestException("操作失败")
            session.transaction.commit()
        }
    }

    /**
     * API: 搜索公共资源
     */
    @GetMapping("search/{keyword}")
    private fun search(@PathVariable keyword: String): Any {
        factory!!.openSession()!!.use {  session ->
            return session.createNativeQuery(
                    "select name, ext, type, size, upload_time, nickname, path from assets, user " +
                            "where name like :keyword and status=0 and assets.uid=user.uid")
                    .setParameter("keyword", "%${ keyword }%")
                    .resultList
        }
    }
}
