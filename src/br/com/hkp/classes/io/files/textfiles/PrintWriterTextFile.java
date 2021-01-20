/*
arquivo PrintWriterTextFile.java criado a partir de 27 de julho de 2018
*/
package br.com.hkp.classes.io.files.textfiles;

import java.io.PrintWriter;
import java.io.Writer;


/**
 * Esta classe estende PrintWriter para fornecer o metodo clearWriteError(),
 * responsavel por limpar o status de erro de IO.
 * 
 * A classe PrintWriter fornece o metodo clearError() para esse fim, porem eh
 * um metodo declarado como protected, e portanto objetos PrintWriter fora do
 * pacote java.io nao podem acessa-lo. Mas objetos da classe PrintWriteTextFile
 * podem acessar o metodo clearWriteError(). Objetos da classe 
 * PrintWriterTextFile possuem as mesmas funcionalidades que os da classe 
 * PrintWriter, mas tambem podem acessar indiretamente clearError() atraves de
 * clearWriteError(). Mesmo se forem declarados em classes fora do pacote
 * java.io
 * 
 * @author Hugo Kaulino Pereira
 * @version 1.0
 * @since 1.0
 */
public class PrintWriterTextFile extends PrintWriter
{
    /**
     * Cria um objeto PrintWriterTextFile, mais especializado em escrever 
     * fluxos de caracteres em arquivos texto.
     * 
     * @param w Qualquer objeto da classe Writer
     * 
     * @since 1.0
     */
    public PrintWriterTextFile(Writer w)
    {
        super(w);
    }//fim de PrintWriterTextFile()
    
    /**
     * Executa a mesma funcao de clearError() na superclasse java.io.PrintWriter
     * 
     * @since 1.0
     */
    public void clearWriteError()
    {
        clearError();
    }//fim de clearWriteError()
    
}//fim da classe PrintWriterTextFile
