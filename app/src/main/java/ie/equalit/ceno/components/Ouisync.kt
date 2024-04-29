package ie.equalit.ceno.components

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.equalitie.ouisync.Repository
import org.equalitie.ouisync.Session
import org.equalitie.ouisync.ShareToken
import org.equalitie.ouisync.File as ouiFile
import java.io.File
import java.nio.charset.Charset


class Ouisync (
    context : Context
) {
    lateinit var session : Session
    var storeDir : String? = null
    var writeToken : ShareToken? = null
    private var sessionError by mutableStateOf<String?>(null)
    private var protocolVersion by mutableStateOf<Int>(0)
    private val rootDir : File? = context.filesDir
    private var configDir : String = "$rootDir/config"
    //TODO: allow storeDir to be chosen by user
    private var repositories by mutableStateOf<Map<String, Repository>>(mapOf())

    fun createSession() {
        try {
            session = Session.create(configDir)
            sessionError = null
        } catch (e: Exception) {
            Log.e(TAG, "Session.create failed", e)
            sessionError = e.toString()
        } catch (e: java.lang.Error) {
            Log.e(TAG, "Session.create failed", e)
            sessionError = e.toString()
        }
    }

    suspend fun getProtocolVersion() : Int {
        session.let {
            protocolVersion = it.currentProtocolVersion()
        }
        return protocolVersion
    }

    private suspend fun createOrOpenRepository(name: String, token: String = "") : Repository {
        val session = this.session
        Log.d(TAG, "creating repository named \"$name\" in $storeDir")

        if (repositories.containsKey(name)) {
            Log.e(TAG, "repository named \"$name\" already exists")
            return openRepository(name)
        }

        var shareToken: ShareToken? = null

        if (token.isNotEmpty()) {
            shareToken = ShareToken.fromString(session, token)
        }

        val repo = Repository.create(
            session,
            "$storeDir/$name.$DB_EXTENSION",
            readSecret = null,
            writeSecret = null,
            shareToken = shareToken,
        )

        writeToken = repo.createShareToken(name = "cenoProfile")

        Log.d(TAG, writeToken.toString())

        repositories = repositories + (name to repo)
        return repo
    }

    private suspend fun openRepository(name: String) : Repository {
        val session = this.session
        val file = File("$storeDir/$name.$DB_EXTENSION")
        val repo = Repository.open(session, file.path)
        Log.i(TAG, "Opened repository $name")
        return repo
    }

    suspend fun openRepositories() {
        val session = this.session
        val files = File(storeDir!!).listFiles() ?: arrayOf()

        for (file in files) {
            if (file.name.endsWith(".$DB_EXTENSION")) {
                try {
                    val name = file
                        .name
                        .substring(0, file.name.length - DB_EXTENSION.length - 1)
                    val repo = Repository.open(session, file.path)

                    Log.i(TAG, "Opened repository $name")

                    repositories = repositories + (name to repo)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to open repository at ${file.path}")
                    continue
                }
            }
        }
    }

    suspend fun createAndWriteToRepo(name: String, contentW : String, charset: Charset = Charsets.UTF_8) {
        val repo = createOrOpenRepository(name)
        Log.i(TAG, "Opened repository $name")
        val fileW = ouiFile.create(repo, "prefs.txt")
        fileW.write(0, contentW.toByteArray(charset))
        fileW.flush()
        fileW.close()
    }

    suspend fun openAndReadFromRepo(name: String) : String {
        Log.d(TAG, "Opening repository $storeDir/$name")
        val session = this.session
        val repo = Repository.open(
            session,
            "$storeDir/$name.$DB_EXTENSION",
        )
        Log.i(TAG, "Opened repository $name")
        val fileR = ouiFile.open(repo, "prefs.txt")
        val len = fileR.length()
        Log.d(TAG, "prefs.txt is $len bytes long")
        val contentR = fileR.read(0, len).toString(Charsets.UTF_8)
        Log.d(TAG, "Got content: $contentR")
        fileR.close()
        return contentR
    }


    companion object {
        private const val TAG = "OUISYNC"
        private val DB_EXTENSION = "ouisyncdb"
    }
}