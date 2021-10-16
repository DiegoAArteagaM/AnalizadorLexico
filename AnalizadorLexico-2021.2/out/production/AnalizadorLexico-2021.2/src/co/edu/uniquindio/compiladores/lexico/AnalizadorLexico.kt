package co.edu.uniquindio.compiladores.lexico

class AnalizadorLexico (var codigoFuente:String) {

    var caracterActual = codigoFuente[0]
    var posicionActual = 0
    var listaTokens = ArrayList<Token>()
    var listaErrores = ArrayList<Error>()
    var finCodigo = 0.toChar()
    var filaActual = 0
    var columnaActual = 0

    fun analizar(){
        while(caracterActual != finCodigo){
            if(caracterActual == ' ' || caracterActual == '\n' || caracterActual == '\t'){
                obtenerSiguienteCaracter()
                continue
            }
            if(esEntero())continue
            if(esDecimal())continue
            if(esIdentificador())continue
            if(esOperadorAritmetico())continue
            if(esOperadorAsignacion())continue
            if(esOperadorIncremento())continue
            if(esOperadorDecremento())continue
            if(esOperadorRelacional())continue
            if(esOperadorLogico())continue
            if(esParentesisDerecho())continue
            if(esParentesisIzq())continue
            if(esLlaveDerecha())continue
            if(esLlaveIzq())continue
            if(esCorcheteDerecha())continue
            if(esCorcheteIzq())continue
            if(esFinSentencia())continue
            if(esPunto())continue
            if(esDosPuntos())continue
            if(esComa())continue
            if(esPalabraReservada())continue
            if(esComentarioLinea())continue
            if(esCadena())continue
            if(esCaracter())continue
            if(esComentarioBloque())continue

            almacenarToken(""+caracterActual, Categoria.DESCONOCIDO, filaActual, columnaActual)
            obtenerSiguienteCaracter()
        }
    }

    fun esEntero():Boolean{
        if(caracterActual.isDigit()){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual
            lexema += caracterActual
            obtenerSiguienteCaracter()

            while(caracterActual.isDigit()){
                lexema += caracterActual
                obtenerSiguienteCaracter()
            }
            if(caracterActual == '.'){
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false
            }
            almacenarToken(lexema, Categoria.ENTERO, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esComentarioBloque():Boolean{
        if(caracterActual == '¿'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual
            lexema += caracterActual
            obtenerSiguienteCaracter()

            while(caracterActual != '?'){

                if(caracterActual == finCodigo){
                    reportarError("Debe cerrar los comentarios de bloque", filaInicial, columnaInicial)
                    obtenerSiguienteCaracter()
                    hacerBT(posicionInicial, filaInicial, columnaInicial)
                    return false
                }

                if(caracterActual == '¿'){
                    reportarError("Debe cerrar los comentarios de bloque", filaInicial, columnaInicial)
                    obtenerSiguienteCaracter()
                    hacerBT(posicionInicial, filaInicial, columnaInicial)
                    return false
                }
                lexema += caracterActual
                obtenerSiguienteCaracter()
            }
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.COMENTARIO_BLOQUE, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esCaracter():Boolean{
        if(caracterActual == '|'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()

            if(caracterActual != '|'){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                if(caracterActual == '\\'){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                    if(caracterActual == 'n' || caracterActual == 't' || caracterActual == 's' || caracterActual == '&' || caracterActual == '\\'){
                        lexema += caracterActual
                        obtenerSiguienteCaracter()
                    }else{
                        reportarError("Luego del signo '\\' solo debe ir 'n, t, s, &, \\", filaInicial, columnaInicial)
                        return false
                    }
                }
                if(caracterActual == '|'){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                    almacenarToken(lexema, Categoria.CARACTER, filaInicial, columnaInicial)
                    return true
                }else{
                    reportarError("Debe cerrar la expresion de caracter", filaInicial, columnaInicial)
                    return false
                }
            }
            reportarError("La expresion caracter no debe ser vacia", filaInicial, columnaInicial)
            return false
        }
        return false
    }

    fun esCadena():Boolean{

        if(caracterActual == '&'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()

            while(caracterActual != '&'){
                if(caracterActual == finCodigo){
                    reportarError("Debe cerrar la cadena", filaInicial, columnaInicial)
                    return false
                }
                lexema += caracterActual
                obtenerSiguienteCaracter()
                if(caracterActual == '\\'){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                    if(caracterActual == 'n' || caracterActual == 't' || caracterActual == 's' || caracterActual == '&' || caracterActual == '\\' || caracterActual == '?' || caracterActual == '¿'){
                        lexema += caracterActual
                        obtenerSiguienteCaracter()
                    }else{
                        reportarError("Luego del signo '\\' solo debe ir 'n, t, s, &, \\", filaInicial, columnaInicial)
                        return false
                    }
                }
            }
            if(caracterActual == '&'){
                lexema += caracterActual
                obtenerSiguienteCaracter()
                almacenarToken(lexema, Categoria.CADENA_CARACTERES, filaInicial, columnaInicial)
                return true
            }else{

            }
        }
        return false
    }

    fun esComentarioLinea():Boolean{
        if(caracterActual == '¡'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual
            lexema += caracterActual
            obtenerSiguienteCaracter()

            if(caracterActual == '¡'){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                while(caracterActual != '\n' && caracterActual != finCodigo){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                }
                almacenarToken(lexema, Categoria.COMENTARIO_LINEA, filaInicial, columnaInicial)
                return true

            }
            hacerBT(posicionInicial, filaInicial, columnaInicial)
            return false
        }

        return false
    }

    fun esPalabraReservada():Boolean{
        if(caracterActual == '_'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual

            lexema += caracterActual
            obtenerSiguienteCaracter()

            if(caracterActual.isLetter()){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                while((caracterActual.isLetter() || caracterActual.isDigit()) && lexema.length < 10){
                    lexema += caracterActual
                    if(buscarLexema(lexema)){
                        obtenerSiguienteCaracter()
                        almacenarToken(lexema, Categoria.PALABRA_RESERVADA, filaInicial, columnaInicial)
                        return true
                    }
                    obtenerSiguienteCaracter()
                }
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false
            }else{
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false
            }
        }
        return false
    }

    fun esPunto():Boolean{
        if(caracterActual == '.'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.PUNTO, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esDosPuntos():Boolean{
        if(caracterActual == ':'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.DOS_PUNTOS, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esComa():Boolean{
        if(caracterActual == ','){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.COMA, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esFinSentencia():Boolean{
        if(caracterActual == '#'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.FIN_SENTENCIA, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esLlaveIzq():Boolean{
        if(caracterActual == '{'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.LLAVE_IZQUIERDA, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esLlaveDerecha():Boolean{
        if(caracterActual == '}'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.LLAVE_DERECHA, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esCorcheteDerecha():Boolean{
        if(caracterActual == ']'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.CORCHETE_DERECHO, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esCorcheteIzq():Boolean{
        if(caracterActual == '['){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.CORCHETE_IZQUIERDO, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esParentesisIzq():Boolean{
        if(caracterActual == '('){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.PARENTESIS_IZQUIERDO, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esParentesisDerecho():Boolean{
        if(caracterActual == ')'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            lexema += caracterActual
            obtenerSiguienteCaracter()
            almacenarToken(lexema, Categoria.PARENTESIS_DERECHO, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esOperadorLogico():Boolean{
        if(caracterActual == '>' || caracterActual == '$' || caracterActual == '¬'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual

            if(caracterActual == '>'){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                if(caracterActual == '>'){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                    almacenarToken(lexema, Categoria.OPERADOR_LOGICO, filaInicial, columnaInicial)
                    return true
                }
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false
            }
            if(caracterActual == '$' || caracterActual == '¬'){
                lexema += caracterActual
                obtenerSiguienteCaracter()
                almacenarToken(lexema, Categoria.OPERADOR_LOGICO, filaInicial, columnaInicial)
                return true
            }
            hacerBT(posicionInicial, filaInicial, columnaInicial)
            return false
        }
        return false
    }

    fun esOperadorRelacional():Boolean{
        if(caracterActual == '>' || caracterActual == '<' || caracterActual == '=' || caracterActual == '!'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual

            if(caracterActual == '<'){
                lexema += caracterActual
                obtenerSiguienteCaracter()
                if(caracterActual == '='){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                    almacenarToken(lexema, Categoria.OPERADOR_RELACIONAL, filaInicial, columnaInicial)
                    return true
                }
                almacenarToken(lexema, Categoria.OPERADOR_RELACIONAL, filaInicial, columnaInicial)
                return true
            }
            if(caracterActual == '>'){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                if(caracterActual == '='){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                    almacenarToken(lexema, Categoria.OPERADOR_RELACIONAL, filaInicial, columnaInicial)
                    return true
                }
                if(caracterActual == '>'){
                    hacerBT(posicionInicial, filaInicial, columnaInicial)
                    return false
                }
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false

            }
            if(caracterActual == '!'){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                if(caracterActual == '='){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                    almacenarToken(lexema, Categoria.OPERADOR_RELACIONAL, filaInicial, columnaInicial)
                    return true
                }else{
                    hacerBT(posicionInicial, filaInicial, columnaInicial)
                    return false
                }
            }
            if(caracterActual == '='){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                if(caracterActual == '='){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                    almacenarToken(lexema, Categoria.OPERADOR_RELACIONAL, filaInicial, columnaInicial)
                    return true
                }else{
                    hacerBT(posicionInicial, filaInicial, columnaInicial)
                    return false
                }
            }
        }
        return false
    }

    fun esOperadorIncremento():Boolean{
        if(caracterActual == '+'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual
            lexema += caracterActual
            obtenerSiguienteCaracter()

            if(caracterActual == '+'){
                lexema += caracterActual
                obtenerSiguienteCaracter()
                almacenarToken(lexema, Categoria.OPERADOR_INCREMENTO, filaInicial, columnaInicial)
                return true
            }else{
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false
            }
        }
        return false
    }

    fun esOperadorDecremento():Boolean{
        if(caracterActual == '-'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual
            lexema += caracterActual
            obtenerSiguienteCaracter()

            if(caracterActual == '-'){
                lexema += caracterActual
                obtenerSiguienteCaracter()
                almacenarToken(lexema, Categoria.OPERADOR_DECREMENTO, filaInicial, columnaInicial)
                return true
            }else{
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false
            }
        }
        return false
    }

    fun esOperadorAritmetico():Boolean{
        if(caracterActual == '+' || caracterActual == '-' || caracterActual == '*' || caracterActual == '/' || caracterActual == '%'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual
            lexema += caracterActual
            obtenerSiguienteCaracter()

            if((caracterActual == '=') || (lexema == "+" && caracterActual == '+') || (lexema == "-" && caracterActual == '-')){
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false
            }
            almacenarToken(lexema, Categoria.OPERADOR_ARITMETICO, filaInicial, columnaInicial)
            return true
        }
        return false
    }

    fun esOperadorAsignacion():Boolean{
        if(caracterActual == '=' || caracterActual == '+' || caracterActual == '-' || caracterActual == '*' || caracterActual == '/' || caracterActual == '%'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual

            if(caracterActual == '='){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                if(caracterActual == '='){
                    hacerBT(posicionInicial, filaInicial, columnaInicial)
                    return false
                }else{
                    almacenarToken(lexema, Categoria.OPERADOR_ASIGNACION, filaInicial, columnaInicial)
                    return true
                }
            }else{
                lexema += caracterActual
                obtenerSiguienteCaracter()

                if(caracterActual == '='){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                    almacenarToken(lexema, Categoria.OPERADOR_ASIGNACION, filaInicial, columnaInicial)
                    return true
                }else{
                    hacerBT(posicionInicial, filaInicial, columnaInicial)
                    return false
                }
            }
        }
        return false
    }

    fun esDecimal():Boolean{
        if(caracterActual.isDigit() || caracterActual == '.'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual

            if(caracterActual == '.'){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                if(caracterActual.isDigit()){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()

                    while(caracterActual.isDigit()){
                        lexema += caracterActual
                        obtenerSiguienteCaracter()
                    }
                    almacenarToken(lexema, Categoria.DECIMAL, filaInicial, columnaInicial)
                    return true
                }
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false
            }else{
                lexema += caracterActual
                obtenerSiguienteCaracter()

                while(caracterActual.isDigit()){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()
                }

                if(caracterActual == '.'){
                    lexema += caracterActual
                    obtenerSiguienteCaracter()

                    if(caracterActual.isDigit()){
                        lexema += caracterActual
                        obtenerSiguienteCaracter()
                        while(caracterActual.isDigit()){
                            lexema += caracterActual
                            obtenerSiguienteCaracter()
                        }
                        almacenarToken(lexema, Categoria.DECIMAL, filaInicial, columnaInicial)
                        return true
                    }else{
                        almacenarToken(lexema, Categoria.DECIMAL, filaInicial, columnaInicial)
                        return true
                    }
                }
            }
        }
        return false
    }

    fun esIdentificador():Boolean{
        if(caracterActual == '_'){
            var lexema = ""
            var filaInicial = filaActual
            var columnaInicial = columnaActual
            var posicionInicial = posicionActual

            lexema += caracterActual
            obtenerSiguienteCaracter()

            if(caracterActual.isLetter()){
                lexema += caracterActual
                obtenerSiguienteCaracter()

                while((caracterActual.isLetter() || caracterActual.isDigit()) && lexema.length < 10){
                    lexema += caracterActual
                    if(buscarLexema(lexema)){
                        hacerBT(posicionInicial, filaInicial, columnaInicial)
                        return false
                    }
                    obtenerSiguienteCaracter()
                }
                almacenarToken(lexema, Categoria.IDENTIFICADOR, filaInicial, columnaInicial)
                return true
            }else{
                hacerBT(posicionInicial, filaInicial, columnaInicial)
                return false
            }
        }
        return false
    }

    fun buscarLexema(lexema:String):Boolean{

        var palabrasReservadas = ArrayList<String>()
        palabrasReservadas.add("_Ciclo")
        palabrasReservadas.add("_Publico")
        palabrasReservadas.add("_Privado")
        palabrasReservadas.add("_Entero")
        palabrasReservadas.add("_Decimal")
        palabrasReservadas.add("_Logico")
        palabrasReservadas.add("_Cadena")
        palabrasReservadas.add("_Caracter")
        palabrasReservadas.add("_ComentB")
        palabrasReservadas.add("_ComentL")
        palabrasReservadas.add("_Variable")
        palabrasReservadas.add("_Metodo")
        palabrasReservadas.add("_Si")
        palabrasReservadas.add("_Sino")
        palabrasReservadas.add("_Metodo")
        palabrasReservadas.add("_Lista")

        for (e in palabrasReservadas){
            if(lexema == e){
                return true;
            }
        }
        return false
    }

    fun almacenarToken(lexema:String, categoria: Categoria, fila:Int, columna:Int) = listaTokens.add(Token(lexema, categoria, fila, columna))

    fun reportarError(error:String, fila:Int, columna:Int) = listaErrores.add(Error(error, fila, columna))

    fun obtenerSiguienteCaracter(){
        if(posicionActual == codigoFuente.length-1){
            caracterActual = finCodigo
        }else{
            if(caracterActual == '\n'){
                filaActual++
                columnaActual=0
            }else{
                columnaActual++
            }
            posicionActual++
            caracterActual = codigoFuente[posicionActual]
        }
    }

    fun hacerBT(posInicial:Int, filaInicial:Int, columnaInicial:Int){
        posicionActual = posInicial
        filaActual = filaInicial
        columnaActual = columnaInicial

        caracterActual = codigoFuente[posicionActual]
    }

}