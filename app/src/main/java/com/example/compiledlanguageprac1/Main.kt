package com.example.compiledlanguageprac1

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.*
import java.nio.file.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Element
import kotlin.system.exitProcess
import java.nio.file.FileStore
import java.nio.file.FileSystems

data class User(val name: String, val age: Int)

fun main() {
    while (true) {
        println("Выберите действие: ")
        println("1. Работа с файлами")
        println("2. Работа с JSON")
        println("3. Работа с XML")
        println("4. Работа с ZIP архивами")
        println("5. Информация о дисках")
        println("6. Выход")

        when (readLine()?.toIntOrNull()) {
            1 -> fileOperations()
            2 -> jsonOperations()
            3 -> xmlOperations()
            4 -> zipOperations()
            5 -> displayDiskInfo()
            6 -> exitProcess(0)
            else -> println("Неверный ввод")
        }
    }
}


// Работа с файлами
fun fileOperations() {
    println("1. Создать файл")
    println("2. Записать в файл")
    println("3. Прочитать файл")
    println("4. Удалить файл")

    when (readLine()?.toIntOrNull()) {
        1 -> {
            println("Введите имя файла:")
            val filename = readLine()!!
            val file = File(filename)
            file.createNewFile()
            println("Файл $filename создан.")
        }
        2 -> {
            println("Введите имя файла:")
            val filename = readLine()!!
            val file = File(filename)
            if (file.exists()) {
                println("Введите текст для записи:")
                val text = readLine()!!
                file.writeText(text)
                println("Текст записан в файл.")
            } else {
                println("Файл не найден.")
            }
        }
        3 -> {
            println("Введите имя файла:")
            val filename = readLine()!!
            val file = File(filename)
            if (file.exists()) {
                println("Содержимое файла:")
                println(file.readText())
            } else {
                println("Файл не найден.")
            }
        }
        4 -> {
            println("Введите имя файла:")
            val filename = readLine()!!
            val file = File(filename)
            if (file.exists()) {
                file.delete()
                println("Файл удален.")
            } else {
                println("Файл не найден.")
            }
        }
        else -> println("Неверный ввод.")
    }
}

// Работа с JSON
fun jsonOperations() {
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    println("Выберите действие:")
    println("1. Создать и записать JSON")
    println("2. Добавить новый объект в JSON")
    println("3. Прочитать JSON")
    println("4. Удалить JSON файл")

    when (readLine()?.toIntOrNull()) {
        1 -> {
            println("Введите имя JSON файла:")
            val filename = readLine()!! + ".json"
            val file = File(filename)
            println("Введите имя пользователя:")
            val name = readLine()!!
            println("Введите возраст пользователя:")
            val age = readLine()!!.toInt()

            val user = User(name, age)
            file.writeText(gson.toJson(mutableListOf(user)))
            println("JSON файл создан и записан.")
        }
        2 -> {
            println("Введите имя JSON файла для добавления объекта:")
            val filename = readLine()!! + ".json"
            val file = File(filename)

            val users: MutableList<User> = if (file.exists()) {
                file.reader().use { reader ->
                    gson.fromJson(reader, object : TypeToken<MutableList<User>>() {}.type)
                }
            } else {
                mutableListOf()
            }

            println("Введите имя пользователя:")
            val name = readLine()!!
            println("Введите возраст пользователя:")
            val age = readLine()!!.toInt()

            users.add(User(name, age))
            file.writeText(gson.toJson(users))
            println("Новый объект добавлен в JSON файл.")
        }
        3 -> {
            println("Введите имя JSON файла:")
            val filename = readLine()!! + ".json"
            val file = File(filename)
            if (file.exists()) {
                val users: List<User> = gson.fromJson(file.reader(), object : TypeToken<List<User>>() {}.type)
                println("Содержимое JSON файла: $users")
            } else {
                println("Файл не найден.")
            }
        }
        4 -> {
            println("Введите имя JSON файла для удаления:")
            val filename = readLine()!! + ".json"
            val file = File(filename)
            if (file.exists()) {
                file.delete()
                println("JSON файл удален.")
            } else {
                println("Файл не найден.")
            }
        }
        else -> println("Неверный ввод.")
    }
}

// Работа с XML
fun xmlOperations() {
    println("1. Создать XML файл")
    println("2. Записать данные в XML")
    println("3. Прочитать XML файл")
    println("4. Удалить XML файл")

    when (readLine()?.toIntOrNull()) {
        1 -> {
            println("Введите имя XML файла:")
            val filename = readLine()!! + ".xml"
            val file = File(filename)

            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = docBuilder.newDocument()

            val rootElement = doc.createElement("users")
            doc.appendChild(rootElement)

            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(DOMSource(doc), StreamResult(file))

            println("XML файл создан.")
        }
        2 -> {
            println("Введите имя XML файла:")
            val filename = readLine()!! + ".xml"
            val file = File(filename)

            if (file.exists()) {
                val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                val doc = docBuilder.parse(file)

                println("Введите имя пользователя:")
                val name = readLine()!!

                println("Введите возраст пользователя:")
                val age = readLine()!!

                val userElement = doc.createElement("user")
                val nameElement = doc.createElement("name")
                nameElement.appendChild(doc.createTextNode(name))
                val ageElement = doc.createElement("age")
                ageElement.appendChild(doc.createTextNode(age))

                userElement.appendChild(nameElement)
                userElement.appendChild(ageElement)
                doc.documentElement.appendChild(userElement)

                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                transformer.transform(DOMSource(doc), StreamResult(file))

                println("Данные записаны в XML файл.")
            } else {
                println("Файл не найден.")
            }
        }
        3 -> {
            println("Введите имя XML файла:")
            val filename = readLine()!! + ".xml"
            val file = File(filename)

            if (file.exists()) {
                val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                val doc = docBuilder.parse(file)


                val users = doc.getElementsByTagName("user")
                for (i in 0 until users.length) {
                    val user = users.item(i) as Element
                    val name = user.getElementsByTagName("name").item(0).textContent
                    val age = user.getElementsByTagName("age").item(0).textContent
                    println("User: Name = $name, Age = $age")
                }
            } else {
                println("Файл не найден.")
            }
        }
        4 -> {
            println("Введите имя XML файла для удаления:")
            val filename = readLine()!! + ".xml"
            val file = File(filename)
            if (file.exists()) {
                file.delete()
                println("XML файл удален.")
            } else {
                println("Файл не найден.")
            }
        }
        else -> println("Неверный ввод.")
    }
}

// Работа с ZIP архивами
fun zipOperations() {
    while (true) {
        println("1. Создать ZIP архив и добавить файл")
        println("2. Добавить файл в существующий ZIP архив")
        println("3. Разархивировать файл")
        println("4. Удалить ZIP архив и его файлы")
        println("5. Выйти")

        when (readLine()?.toIntOrNull()) {
            1 -> createZipArchive()
            2 -> addFileToExistingZip()
            3 -> unzipFile()
            4 -> deleteZipFile()
            5 -> break
            else -> println("Неверный выбор, попробуйте снова.")
        }
    }
}

fun createZipArchive() {
    println("Введите имя ZIP архива:")
    val zipFilename = readLine()!! + ".zip"
    val zipFile = File(zipFilename)

    println("Введите имя файла для добавления в архив:")
    val filename = readLine()!!
    val file = File(filename)

    if (file.exists()) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zipOut ->
            zipOut.putNextEntry(ZipEntry(file.name))
            file.inputStream().use { input ->
                input.copyTo(zipOut)
            }
            zipOut.closeEntry()
        }
        println("ZIP архив '$zipFilename' создан и файл '$filename' добавлен.")
    } else {
        println("Файл '$filename' не найден.")
    }
}

fun addFileToExistingZip() {
    println("Введите имя существующего ZIP архива:")
    val zipFilename = readLine()!! + ".zip"
    val zipFile = File(zipFilename)

    if (!zipFile.exists()) {
        println("Архив '$zipFilename' не найден.")
        return
    }

    println("Введите имя файла для добавления в архив:")
    val filename = readLine()!!
    val file = File(filename)

    if (file.exists()) {
        val tempZipFile = File(zipFile.parent, "temp_${zipFile.name}")

        ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zipIn ->
            ZipOutputStream(BufferedOutputStream(FileOutputStream(tempZipFile))).use { zipOut ->
                while (true) {
                    val entry = zipIn.nextEntry ?: break
                    zipOut.putNextEntry(ZipEntry(entry.name))
                    zipIn.copyTo(zipOut)
                    zipOut.closeEntry()
                }
                zipOut.putNextEntry(ZipEntry(file.name))
                file.inputStream().use { input ->
                    input.copyTo(zipOut)
                }
                zipOut.closeEntry()
            }
        }

        if (zipFile.delete()) {
            tempZipFile.renameTo(zipFile)
            println("Файл '$filename' добавлен в архив '$zipFilename'.")
        } else {
            println("Не удалось удалить старый архив.")
        }
    } else {
        println("Файл '$filename' не найден.")
    }
}

fun unzipFile() {
    println("Введите имя ZIP архива для разархивирования:")
    val zipFilename = readLine()!! + ".zip"
    val zipFile = File(zipFilename)

    if (!zipFile.exists()) {
        println("Архив '$zipFilename' не найден.")
        return
    }

    println("Введите имя директории для распаковки:")
    val outputDir = File(readLine()!!)

    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }

    ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zipIn ->
        var entry: ZipEntry?
        while (zipIn.nextEntry.also { entry = it } != null) {
            entry?.let {
                val outputFile = File(outputDir, it.name)
                FileOutputStream(outputFile).use { output ->
                    zipIn.copyTo(output)
                }
                println("Файл '${it.name}' разархивирован в '${outputFile.absolutePath}'.")
            }
        }
    }
}

fun deleteZipFile() {
    println("Введите имя ZIP архива для удаления:")
    val zipFilename = readLine()!! + ".zip"
    val zipFile = File(zipFilename)

    if (zipFile.exists()) {
        if (zipFile.delete()) {
            println("ZIP архив '$zipFilename' удалён.")
        } else {
            println("Не удалось удалить ZIP архив '$zipFilename'.")
        }
    } else {
        println("Архив '$zipFilename' не найден.")
    }
}

fun displayDiskInfo() {
    println("Информация о логических дисках:")
    for (fileStore in FileSystems.getDefault().fileStores) {
        println("Имя: ${fileStore.name()}")
        println("Тип файловой системы: ${fileStore.type()}")
        println("Общий размер: ${fileStore.totalSpace / (1024 * 1024)} MB")
        println("Доступное пространство: ${fileStore.usableSpace / (1024 * 1024)} MB")
        println("Метка тома: ${fileStore.toString()}")
        println("---------------")
    }
}
