/*
arquivo TextFile.java criado a partir de 26 de julho de 2018
*/
package br.com.hkp.classes.io.files.textfiles;


import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

/**
 * Esta classe fornece uma forma simplificada para criar, acessar e modificar
 * um arquivo texto sequencial. Porem o uso desta classe para ler um arquivo
 * soh eh seguro se o arquivo foi criado e gravado usando os metodos desta 
 * mesma classe e se nao tiver sido editado posteriormente por algum outro meio
 * que nao seja os metodos desta classe.
 * 
 * @author Hugo Kaulino Pereira
 * @version 1.0
 * @since 1.0
 */
public class TextFile 
{
    /**
     * Indica um dos 3 possiveis status do arquivo. Esse valor eh retornado 
     * pelo metodo getFileStatus() se o arquivo estiver aberto para escrita
     * {@link #getFileStatus()} 
     */
    public static final int WRITE = -1;
     /**
     * Indica um dos 3 possiveis status do arquivo. Esse valor eh retornado 
     * pelo metodo getFileStatus() se o arquivo estiver fechado.
     * {@link #getFileStatus()} 
     */
    public static final int CLOSE =  0;
     /**
     * Indica um dos 3 possiveis status do arquivo. Esse valor eh retornado 
     * pelo metodo getFileStatus() se o arquivo estiver aberto para leitura
     * {@link #getFileStatus()} 
     */
    public static final int READ  =  1;
    
    // Os possíveis estados do arquivo: aberto para leitura, para gravacao ou 
    // fechado.
    private static enum Mode {READING, WRITING, CLOSED};
    // O arquivo
    private final File textFile;
    // objeto de saida, quando o arquivo esta aberto para gravacao
    private PrintWriterTextFile out;
    // objeto de entrada: arquivo aberto para leitura.
    private Scanner in;
    // Os 3 possíveis estados assumidos pelo tipo enum Mode
    private Mode status;
    
    /*[01]----------------------------------------------------------------------
    *                        Construtor da classe
    *-------------------------------------------------------------------------*/
    /**
     * Constroi o objeto passando um tipo File como argumento.
     * 
     * @param tf O arquivo a ser manipulado.
     * 
     * @since 1.0
     */
    public TextFile(File tf)  
    {
        textFile = tf;
        status = Mode.CLOSED;
    }//fim de TextFile()
    
    /*[02]----------------------------------------------------------------------
    *                     Abre o arquivo texto para leitura
    *-------------------------------------------------------------------------*/
    /**
     * Abre o arquivo apenas para leitura. O arquivo eh aberto apenas se estiver
     * fechado, caso contrario o metodo nao realiza nenhuma acao.
     * 
     * @throws FileNotFoundException Esta excecao eh lancada se o arquivo nao
     * existir ou nao puder ser acessado.
     * 
     * @since 1.0
     */
    public void openToRead()
        throws FileNotFoundException
    {
        if (status == Mode.CLOSED)
        {
            in = new Scanner(textFile);
            status = Mode.READING;
        }
    }//fim de openToRead()
    
    /*[03]----------------------------------------------------------------------
    *                    Abre o arquivo texto para escrita
    *-------------------------------------------------------------------------*/
    /**
     * Abre o arquivo para gravacao apenas se este estiver fechado. Caso 
     * contrario o metodo nao toma nenhuma acao. Se o arquivo nao existir sera
     * criado. Se existir ,o parametro append define se novos registros ( linhas )
     * serao acrescentadas ao arquivo, ou se ele sera recriado, perdendo-se 
     * entao quaisquer registros anteriores.
     * <p>
     * As operacoes de gravacao sao bufferizadas com um objeto BufferedWriter
     * encapsulado por um objeto PrintWriter se o argumento buffer for passado
     * com valor true.
     * 
     * @param append Se true determina que que os novos registros serao anexados
     * a partir do final do arquivo. Se false o arquivo eh recriado "limpo" para
     * a insercao de novos registros.
     * @param buffer O arquivo pode ser aberto com um buffer para gravacao 
     * associado para agilizar as operacoes de escrita. Para isso o argumento
     * buffer deve ser passado com valor true.
     * 
     * @throws IOException Pode lancar uma excecao caso nao possa ser aberto por
     * algum motivo, como o usuario nao possuir direitos para acessar o arquivo.
     * @throws FileNotFoundException
     * @throws SecurityException Essa excecao foi declarada por precaucao. O 
     * autor da classe nao tem certeza se uma SecurityException pode ser lancada
     * em algum sitema, no caso de acesso negado pelo sistema ao arquivo. No
     * Windows, neste caso, verificou-se que uma FileNotFoundException foi 
     * lancada. De qualquer forma SecurityException eh subclasse de 
     * RuntimeException, que eh uma excecao nao verificada e portanto nao exige
     * uma clausula catch correspondente.
     * 
     * @since 1.0
     * 
     */
    public void openToWrite(boolean append, boolean buffer)
        throws IOException, FileNotFoundException, SecurityException
    {
        if (status == Mode.CLOSED)
        {
            FileWriter fw = new FileWriter(textFile, append);
            if (buffer)
                out = new PrintWriterTextFile(new BufferedWriter(fw));
            else
                out = new PrintWriterTextFile(fw);
            
            status = Mode.WRITING;
        }
    }//fim de openToWrite()
    
    /*[04]----------------------------------------------------------------------
    *                          Fecha o arquivo
    *-------------------------------------------------------------------------*/
    /**
     * Fecha o arquivo apenas se estiver aberto. Seja para leitura ou gravacao.
     * 
     * @throws IOException Se um erro de IO ocorrer ao fechar o arquivo, caso
     * ele tenha sido aberto para gravacao. Se o arquivo foi aberto para 
     * leitura seu fechamento nao pode lancar excecao.
     * 
     * @since 1.0
     */
    public void close()
        throws IOException
    {
        if ( status == Mode.WRITING )
        {
            out.close();
            checkWriteError("Erro ao fechar o arquivo ");
        }
        else if (status == Mode.READING) in.close();
        
        status = Mode.CLOSED;
    }//fim de close()
    
    /*[05]----------------------------------------------------------------------
    *                  Escreve uma linha no arquivo texto
    *-------------------------------------------------------------------------*/
    /**
     * Escreve uma nova linha de texto na ultima posicao do arquivo. Apenas se 
     * ja estiver aberto para gravacao. ATENCAO: se for lancada uma IOException
     * nao eh seguro continuar tentando gravar registros no arquivo, pois sua 
     * integridade pode ter sido corrompida, uma vez que o arquivo, devido ao
     * erro, pode ter sido fechado pelo sistema operacional sem gravar os dados
     * que porventura estivessem no buffer do arquivo. Isso pode ocorrer mesmo
     * se o arquivo nao for aberto para gravacao com a opcao buffer ativada 
     * como true no metodo openToWrite().
     * 
     * @param textLine A linha de texto. Pode ser formatada com varios campos 
     * com o metodo static String.format, usando como token um espaco em branco
     * de separacao enre os dados.
     * @throws IOException Lanca uma IOException se o metodo checkError(), 
     * herdado da superclasse de PrintWriterTextFile ( PrintWriter ) retornar
     * true, indicando que houve erro na gravacao do registro. Entao o estado 
     * de checkError é setado para false antes da excecao ser lancada.
     * 
     * @since 1.0
     */
     public void write(String textLine)
         throws IOException
    {
        if ( status == Mode.WRITING )
        {
            out.print(textLine);
            checkWriteError("Erro ao gravar no arquivo ");
        }
    }//fim de write()
     
    /*[06]----------------------------------------------------------------------
    *                Lê a linha corrente no arquivo texto
    *-------------------------------------------------------------------------*/
    /**
     * Le a linha corrente do arquivo e avanca o ponteiro de leitura para a 
     * proxima linha, se houver. Um tentativa de ler uma linha quando ja foi
     * alcancado o final de arquivo lanca uma excecao noSuchElementException
    * Antes de se tentar fazer a leitura com este metodo, deve ser testado se
    * ja foi alcancado o final do arquivo com o metodo eof().
    * 
    * 
    * @return A linha corrente eh lida e avanco o ponteiro para que a proxima
    * chamada de readln() leia a linha sucessora. Se houver.
    * 
    * @throws NoSuchElementException Na tentativa de ler uma linha depois de 
    * alcancado o fim do arquivo.
    * @throws TextFileNotOpenForReadingException Se o arquivo nao estiver 
    * aberto para a leitura
    * @throws IOException Erro de IO.
    * 
    * @since 1.0
    */
    public String readln()
        throws TextFileNotOpenForReadingException, NoSuchElementException,
               IOException
    {
        if (status != Mode.READING)
            throw new TextFileNotOpenForReadingException
                     (
                        textFile.getName() +
                        " nao foi aberto para leitura"
                     );
        
        return in.nextLine();
        
    }//fim de read()
    
    /*[07]----------------------------------------------------------------------
    *    Indica que a operacao de leitura alcancou o fim do arquivo
    *-------------------------------------------------------------------------*/
    /**
     * Indica se todos os registros ja foram lidos e o final de arquivo
     * alcancado. Deve ser chamado antes de se tentar ler uma linha do 
     * arquivo com readln() {@link #readln()} 
     * 
     * @return true se nao ha mais registros a serem lidos. false caso contrario.
     * Retorna true se o arquivo estiver aberto para gravacao ou fechado.
     * 
     * @since 1.0
     */
    public boolean eof()
    {
        if ( status != Mode.READING )
            return true;
        else
            return !(in.hasNext());
           
    }// fim de eof()
    
    /*[08]----------------------------------------------------------------------
    *             Descarrega o buffer de escrita para o arquivo.
    *-------------------------------------------------------------------------*/
    /**
     * Flush do buffer de gravacao se o objeto estiver no modo WRITING. Ou seja,
     * se o arquivo do objeto foi aberto para gravacao.
     * 
     * @throws IOException Se uma excecao de IO ocorrer
     * 
     * @since 1.0
     */
    public void flushWriteBuffer()
        throws IOException
    {
        if ( status == Mode.WRITING )
        {
            out.flush();
            checkWriteError("Erro ao gravar no arquivo ");
        }
        
    }//fim de flushWriteBuffer()
    
    /*[09]----------------------------------------------------------------------
    *    Checa se houve erro ao gravar ou fechar o arquivo. Nesse caso lanca
    *    uma IOException com mensagem apropriada de erro passada como argumento
    *    ao metodo.
    *-------------------------------------------------------------------------*/
    private void checkWriteError( String msg )
        throws IOException
    {
        if ( out.checkError() ) 
        {
            out.clearWriteError();
            throw new IOException( msg + textFile.getName() );
        }
        
    }//fim de checkWriteError()
    
    /*[10]----------------------------------------------------------------------
    *   Retorna o status do arquivo. Se fechado, aberto para gravacao ou 
    *   aberto para leitura.
    --------------------------------------------------------------------------*/
    /**
     * Permite saber se o arquivo foi aberto para gravacao, leitura, ou se nao
     * foi aberto.
     * 
     * @return -1 se esta aberto para gravacao, 0 se estiver fechado e 1 se 
     * aberto para leitura.
     * 
     * @since 1.0
     */
    public int getFileStatus()
    {
        switch (status)
        {
            case WRITING:
                return WRITE;
            case READING:
                return  READ;
            default: // CLOSED
                return CLOSE; 
        }//fim do switch
    }//fim de getFileStatus()
    
    /*--------------------------------------------------------------------------
    *     Metodo privado usado por main() para escrever no arquivo e tratar
    *     excecoes
    --------------------------------------------------------------------------*/
    private static void writelnToTextFile(TextFile tf, String s)
    {
        Scanner scanner = new Scanner( System.in );
        boolean trying = true,
                abort = false,
                error = false;
        
        System.out.println("Tentando gravar o registro " + s);
        while (trying)
        {
            try
            {
                if (error)
                {
                    error = false;
                    tf.close();
                    tf.openToWrite(true,false);          
                }
                 
                tf.write(s);
                trying = false;
            }
            catch (IOException e)
            {
                error = true;
                
                System.err.println(e);
                System.out.println("Erro ao gravar registro.");
                System.out.print("Tecle 0 para tentar novamente e 1 para abortar.");
                System.out.println(" Então <ENTER>");
                
                int i = scanner.nextInt();
                trying = (i == 0); abort = (i == 1);
   
            }//fim do catch
            finally
            {
                if (abort)
                {
                    System.err.println("Terminado pelo usuario");
                    System.exit(1);
                }
            }
            
        }//fim do while
        
    }//fim de writelnToTextFile()
    
   /**
    * Um programa exemplificando usos da classe.
    * 
    * @param args Nao utilizado
    */
    public static void main(String args[])
    {
        //Inicializa o objeto com o arquivo TextFileTest.txt no diretorio do 
        // usuario.
        String userDir = System.getProperty("user.home").replace('\\','/');
        //userDir = "e:";
        TextFile tf = new TextFile( new File( userDir + "/TextFileTest.txt") );
        
        
        // Abre o arquivo para gravacao no modo "append", para anexar registros
        // aos jah existentes, caso jah exista o arquivo no diretorio especificado.
        // Se nao existir o arquivo sera criado.
        try
        {
            tf.openToWrite(true,true);
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e);
            System.exit(1);            
        }
        catch (IOException e)
        {
            System.err.println(e);
            System.exit(1);
        }
        
        // Escreve linhas no arquivo como registros contendo um campo String com
        // o nome de uma pessoa sucedido por um campo de int com a idade.
        //
        // Usando um espaco como separador de campo, os dados podem posteriormente
        // serem lidos do arquivo recuperando os campos individuais de cada 
        // registro ( ou seja, cada linha do arquivo texto )
        // Para isto use um objeto da classe Scanner comforme o exemplo mostrado
        // abaixo neste mesmo metodo main()
        writelnToTextFile(tf,String.format("%s %d\r\n","Joao",55));
        writelnToTextFile(tf,String.format("%s %d\r\n","Manoel",15));
        
        // Essa pausa permite forcar um erro no dispositivo de IO para testar
        // como o objeto da classe responde a um erro de gravacao no arquivo e
        // como lidar com excecoes deste tipo. Um erro pode ser forcado, por 
        // exemplo, abrindo o arquivo em um pendrive e retirando o pendrive
        // neste ponto, quando o programa pedir ao usuario para teclar ENTER 
        // antes de continuar a gravar mais registros.
        System.out.print("Tecle <ENTER> para continuar o programa\n");
        Scanner scanner = new Scanner (System.in);
        String enter = scanner.nextLine();
               
        writelnToTextFile(tf,String.format("%s %d\r\n","Maria",27));
        writelnToTextFile(tf,String.format("%s %d\r\n","Carlos",10));
              
        // Fecha o arquivo.
        try
        {
            tf.close();
        }
        catch (IOException e)
        {
            System.err.println(e);
            System.exit(1);
        }
        
        
        // Abre o arquivo, agora para leitura.
        try
        {
            tf.openToRead();
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e);
            System.exit(1);
        }
        
        System.out.println("Lendo o arquivo...\n");
        System.out.printf("%-26s%-12s%s\n","Linha do arquivo","Nome","Idade");
        
        // Le todos os registros ate alcancar o fim de arquivo.
        String s = null; Scanner sc;
      
            while (!tf.eof())
            {
                // cada linha eh lida por inteira com readln()
                try
                {
                    s = tf.readln();
                    
                    sc = new Scanner(s);
                    try
                    {
                       System.out.printf
                                 (
                                     "%-26s%-12s%d\n",
                                     s,
                                     sc.next(),
                                     sc.nextInt()
                                 );

                    }
                    catch (NoSuchElementException e)
                    {
                        System.err.println("Registro inválido: " + s);
                        System.exit(1);
                    }
                }
                catch (IOException e)
                {
                    System.err.println("Erro ao ler arquivo.");
                    System.exit(1);
                }
              
            }//fim do while
                     
        // Fecha o arquivo. Como o arquivo foi aberto para leitura nenhuma 
        // excecao sera de fato lancada ao fechar o arquivo.
        try
        {
            tf.close();
        }
        catch (IOException e) {}
        
    }//fim de main()
    
}// fim da classe TextFile