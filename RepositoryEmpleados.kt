package org.example.EjercicioEmpleados

import org.example.EjercicioCrud.Model.empleado
import org.w3c.dom.*
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class RepositoryEmpleados {
    val diccionario = mutableMapOf<String, List<String>>()
    val FicheroPrueba = Path.of("src/main/resources/archivoTexto")

    fun MapaEmpleados(): MutableMap<String, List<String>> {
        val br: BufferedReader = Files.newBufferedReader(FicheroPrueba)
        br.readLine();

        br.use { flujo ->
            flujo.forEachLine { linea ->
                val encabezados = linea.split(",")
                diccionario[encabezados[0]] = listOf(encabezados[1], encabezados[2], encabezados[3])
            }
        }
        return diccionario;
    }

    fun EscribirXML() {
        val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val builder: DocumentBuilder = factory.newDocumentBuilder()
        val imp: DOMImplementation = builder.domImplementation
        val document: Document = imp.createDocument(null, "empleados", null)


        diccionario.forEach { (ID, datos)
            ->
            val empleado: Element = document.createElement("empleado")
            empleado.setAttribute("id", ID)

            val apellidos: Element = document.createElement("apellidos")
            apellidos.textContent = datos[0]
            empleado.appendChild(apellidos)

            val departamento: Element = document.createElement("departamento")
            departamento.textContent = datos[1]
            empleado.appendChild(departamento)

            val salario: Element = document.createElement("salario")
            salario.textContent = datos[2]
            empleado.appendChild(salario)

            document.documentElement.appendChild(empleado)

        }
        val source: Source = DOMSource(document)
        val result: StreamResult = StreamResult(Path.of("src\\main\\resources\\archivoDestino").toFile())
        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.transform(source, result)
    }

    fun cambiarSueldo() {
        val lectura = Path.of("src/main/resources/archivoDestino")
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(lectura.toFile())
        document.documentElement.normalize()

        val root = document.documentElement
        val empleadoNode = root.getElementsByTagName("empleado")

        while (true) {
            println("¿Desea cambiar el salario?")
            println("1-Si")
            println("2-No")
            val opcion = readLine()?.toInt()

            if (opcion == 1) {
                println("Introduce el ID a modificar")
                val idBuscar = readLine()?.toString()

                println("Introduce el nuevo salario")
                val nuevoSalario = readLine()?.toInt()

                var encontrado = false
                for (i in 0 until empleadoNode.length) {
                    val empleado = empleadoNode.item(i)
                    val id = empleado.attributes.getNamedItem("id")?.nodeValue

                    if (id == idBuscar) {
                        val salarioNode = root.getElementsByTagName("salario").item(0)
                        salarioNode.textContent = nuevoSalario.toString()
                        encontrado = true
                        break
                    }
                }

                if (encontrado) {
                    val transformerFactory = TransformerFactory.newInstance()
                    val transformer = transformerFactory.newTransformer()
                    val source = DOMSource(document)
                    val result = StreamResult(lectura.toFile())
                    transformer.transform(source, result)

                    println("Salario actualizado correctamente.")
                } else {
                    println("ID no encontrada")
                }
            } else if (opcion == 2) {
                break
            }
        }
    }

    fun mostrarXML() {
        val lectura = Path.of("src/main/resources/archivoDestino")
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(lectura.toFile())

        val root = document.documentElement
        val empleados = root.getElementsByTagName("empleado")

        for (i in 0 until empleados.length) {
            val empleado = empleados.item(i) as Element
            val id = empleado.getAttribute("id")
            val apellidos = empleado.getElementsByTagName("apellidos").item(0).textContent
            val departamento = empleado.getElementsByTagName("departamento").item(0).textContent
            val salario = empleado.getElementsByTagName("salario").item(0).textContent

            println("ID: $id, Apellido: $apellidos, Departamento: $departamento, Salario: $salario")
        }
    }

        }



