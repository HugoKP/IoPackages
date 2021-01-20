/*
arquivo TextFileNotOpenForReadingException.java criado a partir de 
26 de julho de 2018
*/
package br.com.hkp.classes.io.files.textfiles;

/**
 * Esta classe foi criada para lancar um objeto de excecao quando o metodo 
 * br.com.bkp.classes.files.textfiles.TextFile.readln() tentar ler a linha
 * de um arquivo texto nao aberto para leitura.
 * <p>
 * A excecao deve ser lancada antes que um metodo tente fazer a leitura
 * de um arquivo que nao esta aberto  para a leitura.
 * 
 * @author Hugo Kaulino Pereira
 * @version 1.0
 * @since 1.0
 *
 */
public class TextFileNotOpenForReadingException extends RuntimeException
{
    /**
     * Construtor da classe.
     * 
     * @param message Mensagem descritiva do erro. Preferencialmente indicando o 
     * nome do arquivo que causou a excecao. 
     * 
     * @since 1.0
     */
    public TextFileNotOpenForReadingException(String message)
    {
        super(message);
    }//fim de TextFileNotOpenForReadingException()
    
}//fim da classe TextFileNotOpenForReadingException
