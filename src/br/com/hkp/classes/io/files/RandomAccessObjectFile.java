/*
Arquivo RandomAccessObjectFile.java criado a partir de 26 de marco de 2019.
*/
package br.com.hkp.classes.io.files;

import java.io.*;


/**
 * Esta classe permite criar objetos gravam e leem em um arquivo de acesso 
 * direto. Neste tipo de arquivo manipulado por esta classe, apenas objetos
 * podem ser lidos ou gravados.
 * <p>
 * Se o arquivo for gravado com objetos de uma classe de um determinado pacote,
 * e depois tentar ser lido para objetos que sejam identicos (mesmos campos) mas
 * pertencentes a classe de algum outro pacote, ocorrerah um erro em tempo de 
 * execucao. Os objetos gravados e lidos no arquivo devem pertencer todos a 
 * mesma classe.
 * 
 * @author Hugo Kaulino Pereira
 * @version 1.0
 * @since 1.0
 */
public final class RandomAccessObjectFile
{
    private final File file;
    private RandomAccessFile randomFile;
    private final int recordLength;
    private final byte[] arrayObject; 
    
    private static enum State{CLOSE, OPEN};
    private State state;
    
    /**
     * Constroi um objeto para gravar e ler com acesso direto em um arquivo onde
     * se pode inserir e recuperar objetos. Deve ser fornecido a este construtor
     * o arquivo como um objeto File e o tamanho do registro que deve ser o 
     * tamanho em bytes do tipo de objeto que serah inserido e recuperado no 
     * arquivo.
     * <p>
     * Este tamanho pode ser obtido com o metodo static
     * {@link #objectLength(java.lang.Object)} desta classe. Como se trata de 
     * arquivo de acesso direto eh necessario que todos os objetos tenham o 
     * mesmo tamanho em bytes. Portanto eh preciso cuidar para que todos os 
     * objetos a serem inseridos neste tipo de arquivo tenham todos os campos 
     * sempre com o mesmo tamanho. Um campo tipo String, por exemplo, deve ter
     * conteudo sempre com o mesmo numero de caracteres em todos os objetos.
     * 
     * @param f O arquivo que serah criado ou atualizado.
     * @param recLength O tamanho em bytes de cada registro.
     */
    /*[00]----------------------------------------------------------------------
    *
    --------------------------------------------------------------------------*/
    public RandomAccessObjectFile(File f, int recLength)  
    {
        file = f;
        recordLength = recLength;
        arrayObject = new byte[recordLength];
        state = State.CLOSE;
    }//fim do construtor RandomAccessObjectFile()
    
    /*[01]----------------------------------------------------------------------
    *            Converte um objeto em um array de bytes
    --------------------------------------------------------------------------*/
    private static byte[] objectToBytes(Object obj)
        throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
       
        oos.writeObject(obj);
    
        bos.close();
        oos.close();
        
        return bos.toByteArray();
       
    }//fim de objectToBytes()
    
    /*[02]----------------------------------------------------------------------
    *              Converte um array de bytes em objeto.
    --------------------------------------------------------------------------*/
    private static Object bytesToObject(byte[] objByteArray)
        throws IOException, ClassNotFoundException
    {
        return new ObjectInputStream
                   (
                       new ByteArrayInputStream(objByteArray)
                   ).readObject(); 
    }//fim de bytesToObject()
    
    /**
     * Retorna o tamanho, em bytes, que um registro de um objeto de uma classe
     * especifica ocuparah no arquivo. Um arquivo deste tipo soh deve conter
     * registros de objetos de uma mesma classe e eh necessario garantir que 
     * estes objetos ocupem todos o mesmo tamanho em bytes na memoria. Ou seja,
     * eh necessario garantir que todos os campos da classe que nao sejam 
     * marcados como transient, tenham sempre um mesmo tamanho em qualquer 
     * instancia desta classe para que se possa criar um arquivo de acesso 
     * direto soh com registros de objetos desta classe.
     * 
     * @param obj Um objeto qualquer.
     * 
     * @return O tamanho em bytes que um registro deste objeto ocuparia no 
     * arquivo.
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    /*[03]----------------------------------------------------------------------
    *        Retorna o tamanho em bytes que um objeto ocupara no arquivo.
    --------------------------------------------------------------------------*/
    public static int objectLength(Object obj)
        throws IOException, ClassNotFoundException
    {
        return objectToBytes(obj).length;
    }//fim de objectLength()
    
    /**
     * Le um registro no arquivo na posicao indicada. A posicao 0 indica o 
     * primeiro registro. A posicao {@link #fileLength() ) - 1 indica a posicao
     * do ultimo registro do arquivo.
     * 
     * @param pos A posicao do registro a ser lido.
     * 
     * @return O objeto lido do registro. Deve sofrer uma coercao para o tipo
     * apropriado de classe quando este metodo retornar o objeto.
     * 
     * @throws IOException
     * @throws ClassNotFoundException 
     */   
    /*[04]----------------------------------------------------------------------
    *
    --------------------------------------------------------------------------*/
    public Object readFile(int pos)
        throws IOException, ClassNotFoundException
    {
        if (!isOpen()) return null;
        
        randomFile.seek(pos * recordLength);
        randomFile.read(arrayObject);
        
        return bytesToObject(arrayObject);

    }//fim de readFile()
       
    /**
     * Grava um registro na posicao indicada. Se esta posicao for passada com 
     * valor negativo o registro a ser gravado serah acrescentado no fim do 
     * arquivo. Se a posicao for um indice positivo mas invalido serah lancada
     * uma IOException. 0 indica a primeira posicao no arquivo. A posicao
     * {@link #fileLength() ) - 1 indica a posicao do ultimo registro do 
     * arquivo. 
     * 
     * @param obj Um objeto qualquer a ser gravado. Todos os objetos que forem
     * gravados em um arquivo deste tipo devem ser da mesma classe e terem 
     * todos o mesmo tamanho em bytes.
     * 
     * @param pos A posicao onde serah gravado o registro.
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    /*[05]----------------------------------------------------------------------
    *
    --------------------------------------------------------------------------*/
    public void writeFile(Object obj, int pos)
        throws IOException, ClassNotFoundException
    {
        if (!isOpen()) return;
      
        if (pos >= 0)
            randomFile.seek(pos * recordLength);
        else
            randomFile.seek(file.length());
      
        randomFile.write(objectToBytes(obj));
    
    }//fim de writeFile()
    
    /**
     * Abre o arquivo para leitura e escrita.
     * 
     * @throws IOException 
     */
    /*[06]----------------------------------------------------------------------
    *
    --------------------------------------------------------------------------*/
    public void open()
        throws IOException
    {
        if (!isOpen())
        {
            randomFile = new RandomAccessFile(file, "rw");
            state = State.OPEN;
        }
    }//fim de open()
    
    /**
     * Fecha o arquivo.
     * 
     * @throws IOException 
     */
    /*[07]----------------------------------------------------------------------
    *
    --------------------------------------------------------------------------*/
    public void close()
        throws IOException
    {
        if (isOpen())
        {
            randomFile.close();
            state = State.CLOSE;
        }
    }//fim de close()
    
    /**
     * Retorna true se o arquivo estiver aberto. False se nao.
     * 
     * @return Retorna true se o arquivo jah estiver aberto.
     */
    /*[08]----------------------------------------------------------------------
    *
    --------------------------------------------------------------------------*/
    public boolean isOpen()
    {
        return state.equals(State.OPEN);
    }//fim de isOpen()
    
    /**
     * Retorna O tamanho do arquivo em registros.
     * 
     * @return Quantos registros ha no arquivo. Incluindo os registros em 
     * branco.
     */
    /*[09]----------------------------------------------------------------------
    *
    --------------------------------------------------------------------------*/
    public long fileLength()
    {
        return file.length() / recordLength;
    }//fim de fileLength()
    
    /**
     * Retorna o tamanho do registro em bytes.
     * 
     * @return O tamanho em bytes do registro.
     */
    /*[10]----------------------------------------------------------------------
    *
    --------------------------------------------------------------------------*/
    public int recordLength()
    {
        return recordLength;
    }//fim de recordLength()
    
    /**
     * Metodo com exemplos de uso da classe.
     * 
     * @param args Nao utilizado.
     */
    /*[11]----------------------------------------------------------------------
    *
    --------------------------------------------------------------------------*/
    public static void main(String[] args)
    {
        NewClass obj = new NewClass();
        RandomAccessObjectFile rf = null;
        try
        {
           rf = new RandomAccessObjectFile
                    (
                        new File("Teste.ser"), objectLength(obj)
                    );
            
            System.out.println("++++"+rf.recordLength());
                                 
            rf.open();
            
            System.out.println("file length "+rf.fileLength());
           
            for (int i = 0; i <= 10; i++)
                rf.writeFile(obj, -1);
           
            System.out.println("file length "+rf.fileLength());
                              
            obj = null;
            obj = (NewClass)rf.readFile(5);
           
            System.out.println
            (
                "Obj. lido do arquivo: " + obj.i + " " + obj.d+obj.f
            );
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
        finally
        {
            try
            {
               if (rf != null) rf.close(); 
            }
            catch (IOException e)
            {
                System.err.println("Erro ao fechar o arquivo.");
            }
        }
  
    }//fim de main()
    
}//fim da classe RandomAccessObjectFile

