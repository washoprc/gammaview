/*
    GAMMA Viewer - FTPConnection : FTP implementation
    Copyright (C) 2002-2003  Tsuda Eisuke


    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


    GAMMA Viewer - FTPConnection : FTPサーバへのコネクションを行うクラス
    Copyright (C) 2002-2003  津田英介


    このプログラムはフリーソフトウェアです。あなたはこれを、フリーソフ
    トウェア財団によって発行された GNU 一般公衆利用許諾契約書(バージョ
    ン2か、希望によってはそれ以降のバージョンのうちどれか)の定める条件
    の下で再頒布または改変することができます。

    このプログラムは有用であることを願って頒布されますが、*全くの無保
    証* です。商業可能性の保証や特定の目的への適合性は、言外に示された
    ものも含め全く存在しません。詳しくはGNU 一般公衆利用許諾契約書をご
    覧ください。

    あなたはこのプログラムと共に、GNU 一般公衆利用許諾契約書の複製物を
    一部受け取ったはずです。もし受け取っていなければ、フリーソフトウェ
    ア財団まで請求してください(宛先は the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA)。
*/


package gammaviewer.Net;

import gammaviewer.InfomationException;

//import java.util.Vector;
import java.net.InetAddress;
import java.net.Socket;
import java.io.*;


public class FTPConnection implements ServerConnection
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  // FTP controller socket (port 21)
  Socket FtpSocket = null;

  // controller Buffer reader/writer
  BufferedReader Reader = null;
  BufferedWriter Writer = null;


  //-----------------------------------------
  /*  Constructors  */
  //-----------------------------------------

  public FTPConnection()
  {
  }

  protected void finalize()
  {
    // 念のため ログアウトを呼びます。
    logout();
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : Send FTP command
  // Argument  : Command to send
  // Return    : void
  private void writeCommand( String autoCommand ) throws IOException
  {
    try
    {
      if( Writer != null )
      {
        // Write given command into stream.
        // Why not using Writer.newLine()
        // Because, server is not same OS as running machine ( FTP is almost running on UNIX );
        Writer.write( autoCommand + "\n" );
        Writer.flush();
      }
    }
    catch ( IOException autoIOExcpt )
    {
      throw new IOException( "reason: " + autoIOExcpt.toString() + " Command: " + autoCommand );
    }
  }


  // Purpose   : Create session to remote host
  // Argument  : IP address and remote port of remote host
  // Return    : void
  public void CreateConnection( InetAddress autoAddress, int autoPort ) throws InfomationException
  {
    try
    {
      FtpSocket = new Socket( autoAddress , autoPort );

      // Timeout is 60 second
      FtpSocket.setSoTimeout( 60 * 1000 );

      Writer = new BufferedWriter( new OutputStreamWriter( FtpSocket.getOutputStream() ) );
      Reader = new BufferedReader( new InputStreamReader( FtpSocket.getInputStream() ) );

      // Get connection response
      String autoResponse = Reader.readLine();
      if( ! autoResponse.startsWith("220") )
        throw new InfomationException( "FTPサーバが何らかの理由で接続を拒否しました" );
    }
    catch( IOException Excpt )
    {
      throw new InfomationException( "ソケットの生成中にネットワークエラーが発生。FTPコネクションが確立できません" );
    }
  }


  // Purpose   : Login
  // Argument  : User name and password
  // Return    : true if able to login, false if fault
  public boolean login( String autoUserName, String autoPassword ) throws InfomationException
  {
    try
    {
      // Input user name
      writeCommand("USER "+ autoUserName );
      String autoResponse = Reader.readLine();
      if( ! autoResponse.startsWith("331") )
        return false;   // Failture to login

      // Input password
      writeCommand("PASS " + autoPassword );
      autoResponse = Reader.readLine();
      if( ! autoResponse.startsWith("230") )
        return false;       // bad password
    }
    catch( IOException autoIOExcpt )
    {
      throw new InfomationException( "ログイン中にネットワークエラーが発生。FTPコネクションが確立できません" );
    }
    return true;
  }


  // Purpose   : To logout safty and close session
  // Argument  : void
  // Return    : void
  public void logout() { closeConnection(); }
  synchronized private void closeConnection()
  {
    try
    {
      if( FtpSocket != null )
      {
        // First abort data transfer
        writeCommand("ABOR");
        Reader.readLine();
        // Finish ftp connection
        writeCommand("QUIT");
        FtpSocket.close();
      }
    }
    catch( IOException autoIOExcpt ) {}

    try
    {
      if( Reader != null )
        Reader.close();
    }
    catch( IOException autoIOExcpt ) {}

    try
    {
      if( Writer != null )
        Writer.close();
    }
    catch( IOException autoIOExcpt ) {}
  }

/*
  // Purpose   : Port command implementation, in this app, this command is not used
  // Argument  : void
  // Return    : Server socket to wait to be connected
  ServerSocket port() throws InfomationException
  {
    // Socket for port connection
    ServerSocket DataSocket = null;
    try
    {
      // open localport
      DataSocket = new ServerSocket(0,1);
      int LocalPort = DataSocket.getLocalPort();
      byte[] address = FtpSocket.getLocalAddress().getAddress();

      StringBuffer PortBuf = new StringBuffer("PORT ");

      for( int i = 0; i < 4; i++ )
      {
        PortBuf.append( address[i] );
        PortBuf.append( "," );
      }
      PortBuf.append( LocalPort % 256 );
      PortBuf.append( "," );
      PortBuf.append( LocalPort & 256 );

      writeCommand( PortBuf.toString() );
      String autoResponse = Reader.readLine();
      if( autoResponse.startsWith("200") != true )
        throw new InfomationException("PORTコマンド失敗");
    }
    catch(IOException autoIOExcpt)
    {
      throw new InfomationException( "Error: " + autoIOExcpt.toString());
    }
    return DataSocket;
  }
*/

  // Purpose   : Implementation of passive command
  // Argument  : void
  // Return    : void
  public Socket passive() throws InfomationException
  {
    try
    {
      // Send passive command
      writeCommand( "PASV" );
      String autoResponse = Reader.readLine();
      if( ! autoResponse.startsWith("227") )
        throw new InfomationException( "Passive modeでの接続に失敗しました。" );
        // Error under connection by passive

      // To get port number, analize response
      //
      // ex, passive mode response
      //   "227 Entering Passive Mode (10,0,0,1,156,115)"
      // 10.0.0.1 is IP
      // 156 * 256 + 115 is port
      int RightBracket = autoResponse.lastIndexOf( ')' );
      int Camma1 = autoResponse.lastIndexOf( ',', RightBracket - 1 );
      int Camma2 = autoResponse.lastIndexOf( ',', Camma1 - 1 );

      int number1 = Integer.parseInt( autoResponse.substring( Camma2 + 1, Camma1 ) );
      int number2 = Integer.parseInt( autoResponse.substring( Camma1 + 1, RightBracket ) );
      int portnumber = number1 * 256 + number2;

      // Create passive mode socket
      Socket PassiveSocket = new Socket( FtpSocket.getInetAddress(), portnumber );

      return PassiveSocket;
    }
    catch( IOException autoIOExcpt )
    {
      throw new InfomationException( "passive modeの設定中にネットワークエラーが発生。コネクションが確立できません" );
    }
  }


  // Purpose   : Change transport type to ascii
  // Argument  : void
  // Return    : true if succeed, false if fault
  public boolean ascii() throws InfomationException
  {
    try
    {
      writeCommand("TYPE A N");
      String autoResponse = Reader.readLine();
      if( ! autoResponse.startsWith("200") )
        throw new InfomationException( "Asciiモード変更に失敗" );
    }
    catch(IOException autoIOExcpt)
    {
      throw new InfomationException( "ASCII modeの設定中にネットワークエラーが発生。コネクションが確立できません" );
    }
    return true;
  }


  // Purpose   : Change transport type to binary
  // Argument  : void
  // Return    : true if succeed, false if fault
  public boolean binary() throws InfomationException
  {
    try
    {
      writeCommand("TYPE I");
      String autoResponse = Reader.readLine();
      if( ! autoResponse.startsWith("200") )
        throw new InfomationException( "Binaryモード変更に失敗" );
    }
    catch(IOException autoIOExcpt)
    {
      throw new InfomationException( "BINARY modeの設定中にネットワークエラーが発生。コネクションが確立できません" );
    }
    return true;
  }


  // Purpose   : Change directory
  // Argument  : String of change directory
  // Return    : true if succeed, false if fault
  public boolean cd( String Directory ) throws InfomationException
  {
    try
    {
      if( Directory == null ) // pwd command
      {
        writeCommand("PWD");
        String autoResponse = Reader.readLine();
        if( autoResponse.startsWith("257") != true )
          throw new InfomationException("PWDコマンド失敗");
      }
      else if( Directory.equals("..") ) // cd to parent directory
      {
        writeCommand("CDUP");
        String autoResponse = Reader.readLine();
        if( autoResponse.startsWith("250") != true )
          throw new InfomationException("CDUPコマンド失敗");
      }
      else // cd to somewhere
      {
        writeCommand( "CWD " + Directory );
        String autoResponse = Reader.readLine();
        if( autoResponse.startsWith("250") != true )
          throw new InfomationException("CWDコマンド失敗");
      }
    }
    catch(IOException autoIOExcpt)
    {
      throw new InfomationException( "ディレクトリ変更中にネットワークエラーが発生。コネクションが確立できません" );
    }
    return true;
  }


  // Purpose   : List command
  // Argument  : void
  // Return    : String of list
  public String ls( boolean autoUseNLST ) throws InfomationException
  {
    try
    {
      // Send PASV command
      Socket autoPassiveSocket = this.passive();

      if( autoUseNLST )
        writeCommand("NLST");
      else
        writeCommand("LIST");

      String autoResponse = Reader.readLine();
      if( ! autoResponse.startsWith("150") )
        throw new InfomationException("NLSTコマンド失敗");

      // Create recearve stream
      BufferedReader autoReadData = new BufferedReader( new InputStreamReader( autoPassiveSocket.getInputStream() ) );

      // Read data
      StringBuffer autoLsContents = new StringBuffer();
      while( ( autoResponse = autoReadData.readLine() ) != null )
        autoLsContents.append( autoResponse );

      autoResponse=Reader.readLine();
      if( ! autoResponse.startsWith("226") )
        throw new InfomationException("転送失敗");

      autoPassiveSocket.close();
      autoReadData.close();

      return autoLsContents.toString();
    }
    catch(IOException autoIOExcpt)
    {
      throw new InfomationException("ファイル一覧の転送中にネットワークエラーが発生。コネクションが確立できません");
    }
  }


  // Purpose   : Get remote file
  // Argument  : Path of remote file
  // Return    : Vector that elements are integer, if file not found, throws FileNotFoundException
  public ByteArrayOutputStream get( String autoFileName )
                          throws InfomationException,
                                 FileNotFoundException
  {
    try
    {
      // Set binary mode
      this.binary();

      // Send PASV command
      Socket autoPassiveSocket = this.passive();

      // Send get command
      writeCommand( "RETR "+ autoFileName );
      String autoResponse = Reader.readLine();

      // If there is no file ( File Not Found )
      if( autoResponse.startsWith("550") )
        throw new FileNotFoundException();

      // Other error.
      if( ! autoResponse.startsWith("150") )
        throw new InfomationException( "ファイルの転送が拒否されました" );

      InputStream autoReadData = autoPassiveSocket.getInputStream();
      ByteArrayOutputStream autoOutputStream = new ByteArrayOutputStream( 9000 );

      // Get datas
      int i = 0;
      while( ( i = autoReadData.read() ) != -1 )
        autoOutputStream.write( i );

      // Close
      autoPassiveSocket.close();

      // Check response
      autoResponse = Reader.readLine();
      if( ! autoResponse.startsWith("226") )
        throw new InfomationException("サーバ側のエラーで転送が完了しませんでした。");

      // Close
      //autoPassiveSocket.close();

      return autoOutputStream;
    }
    catch( InfomationException autoInfoExcpt )
    {
      throw autoInfoExcpt;
    }
    catch( FileNotFoundException autoFNFExcpt )
    {
      throw autoFNFExcpt;
    }
    catch( IOException autoIOExcpt )
    {
      throw new InfomationException( "ファイルの転送中にネットワークエラーが発生。コネクションが確立できません" );
    }
  }
}