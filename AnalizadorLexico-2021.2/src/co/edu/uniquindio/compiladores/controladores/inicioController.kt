package co.edu.uniquindio.compiladores.controladores

import co.edu.uniquindio.compiladores.lexico.AnalizadorLexico
import co.edu.uniquindio.compiladores.lexico.Token
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import java.net.URL
import java.util.*

class inicioController : Initializable{

    @FXML lateinit var campoTexto:TextArea
    @FXML lateinit var tabPane:TabPane
    @FXML lateinit var tabLexico: Tab
    @FXML lateinit var tablaLexico:TableView<Token>
    @FXML lateinit var colLexema: TableColumn<Token, String>
    @FXML lateinit var colCategoria: TableColumn<Token, String>
    @FXML lateinit var colFila: TableColumn<Token, Int>
    @FXML lateinit var colColumna: TableColumn<Token, Int>
    @FXML lateinit var tabErrores:Tab
    @FXML lateinit var tablaErrores:TableView<co.edu.uniquindio.compiladores.lexico.Error>
    @FXML lateinit var colError: TableColumn<Error, String>
    @FXML lateinit var colFilaError: TableColumn<Error, Int>
    @FXML lateinit var colColumnaError: TableColumn<Error, Int >


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        colLexema.cellValueFactory = PropertyValueFactory("lexema")
        colCategoria.cellValueFactory = PropertyValueFactory("categoria")
        colFila.cellValueFactory = PropertyValueFactory("fila")
        colColumna.cellValueFactory = PropertyValueFactory("columna")

        colError.cellValueFactory = PropertyValueFactory("error")
        colFilaError.cellValueFactory = PropertyValueFactory("fila")
        colColumnaError.cellValueFactory = PropertyValueFactory("columna")
    }

    @FXML
    fun analizarCodigo (e : ActionEvent){

        if(campoTexto.text.length > 0){
            val lexico = AnalizadorLexico(campoTexto.text)
            lexico.analizar()

            tablaErrores.items = FXCollections.observableArrayList(lexico.listaErrores)
            tablaLexico.items = FXCollections.observableArrayList(lexico.listaTokens)
        }
    }


}