/*
    GAMMA Viewer - Host : Host infomation
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


    GAMMA Viewer - Host : ホスト情報クラス
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

package gammaviewer.Data;

import gammaviewer.ViewerConstants;
import gammaviewer.InfomationException;
import gammaviewer.Net.*;

import java.io.*;
import java.util.Vector;
import javax.swing.JOptionPane;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Host implements ViewerConstants
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  // Remote host name and address
  String HostName = null; // ex "kasumi"

  // Remote port
  int ConnectionPort = 21; // Default  ftp

  // User name, Password
  String UserName = null;
  String Password = null;

  // Path to shot directry
  String ShotPath = new String( "/usr2/shot/" );

  // Server type ( ex. ftp, smb, or atalk )
  int ServerType = TYPE_FTP;

  //-----------------------------------------
  /*  Constructors  */
  //-----------------------------------------

  public Host()
  {
    HostName = new String( "kasumi.prc.tsukuba.ac.jp" );
    ConnectionPort = 21;
    UserName = new String( "guest" );
    Password = new String( "" );
    ShotPath = new String( "/usr2/shot/" );
    ServerType = ViewerConstants.TYPE_FTP;
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------

  // gets and sets
  public String       getName()           { return HostName; }
  public InetAddress  getAddress() throws UnknownHostException
  {
    // If this object shows localhost, then forcibly return local address
    if( ServerType == ViewerConstants.TYPE_LOCAL )
      return InetAddress.getLocalHost();
    // Else return each host address
    return InetAddress.getByName( HostName );
  }
  public int          getConnectionPort()    { return ConnectionPort; }
  public String       getUserName()       { return UserName; }
  public String       getPassword()       { return Password; }
  public String       getShotPath()       { return ShotPath; }
  public int          getServerType()     { return ServerType; }
  public ServerConnection getConnectClass()
  {
    if( ServerType == TYPE_FTP )
      return new FTPConnection();
    if( ServerType == ViewerConstants.TYPE_LOCAL )
      return new LocalHost();
    else
      return new FTPConnection();
  }

  public void setHost( String autoName ) throws InfomationException
  {
    if( autoName.length() < 1 )
      throw new InfomationException( "サーバ名が空欄になっています。" );
    else
      HostName = autoName;
  }
  public void setConnectionPort( int autoPort ) throws InfomationException
  {
    if( autoPort <= 0 )
      throw new InfomationException( "ポートに負の値は設定できません。" );
    else
      ConnectionPort = autoPort;
  }
  public void setUserName( String NewUser )
  { UserName = NewUser; }
  public void setPassword( String Newpasswd )
  { Password = Newpasswd; }
  public void setShotPath( String NewPath )
  { ShotPath = NewPath; }
  public void setServerType( int type )
  { ServerType = type; }


  public void copyContents( Host autoHost )
  {
    // Copy all objects
    HostName        = new String( autoHost.getName() );
    ConnectionPort  = autoHost.getConnectionPort();
    UserName        = new String( autoHost.getUserName() );
    Password        = new String( autoHost.getPassword() );
    ShotPath        = new String( autoHost.getShotPath() );
    ServerType      = autoHost.getServerType();
  }


  // Purpose   : Unserialize this class
  // Argument  : Vector that element is formatted data
  // Return    : void
  // NOTE      : Contents format is below
  //
  //             /*--------- xxxx -------------*/          header
  //             kay      value                            key, tab, tab, tab, value
  //                ...
  //             kay      value                            key, tab, tab, tab, value
  //
  public void unserialize( Vector autoContents ) throws IOException
  {
    // String that read
    String autoContent = null;
    // Key and value
    String autoKey = null;
    String autoValue = null;

    // Unknown parameter
    boolean autoUnknownParameter = false;
    Vector autoUnknowKeys = new Vector( 2, 2 );

    // Read key and value
    for( int i = 1; i < autoContents.size(); i++ )
    {
      autoContent = (String)autoContents.elementAt(i);
      // Check there is a tab
      if( autoContent.indexOf( "\t" ) < 0 )
      {
        autoUnknowKeys.addElement( autoContent );
        autoUnknownParameter = true;
      }
      else
      {
        // Get kay and
        autoKey = autoContent.substring( 0, autoContent.indexOf( "\t" ) );
        autoValue = autoContent.substring( autoContent.lastIndexOf( "\t" ) + 1 );

        if( autoKey.equals( "HostName" ) )
          HostName = autoValue;

        else if( autoKey.equals( "ConnectionPort" ) )
          ConnectionPort = Integer.parseInt( autoValue );

        else if( autoKey.equals( "UserName" ) )
          UserName = autoValue;

        else if( autoKey.equals( "Password" ) )
          Password = this.deCrypting( autoValue );

        else if( autoKey.equals( "ShotPath" ) )
          ShotPath = autoValue;

        else if( autoKey.equals( "ServerType" ) )
          ServerType = Integer.parseInt( autoValue );

        else if( autoKey.equals( "/*" ) ) // Comment
          ;

        else
        {
          autoUnknowKeys.addElement( autoKey );
          autoUnknownParameter = true;
        }
      }
    }

    if( autoUnknownParameter )
    {
      Object[] autoMessage = new Object[ autoUnknowKeys.size() + 2 ];
      autoMessage[0] = "設定ファイル中でホスト設定の読み込み時に不明なパラメータを見つけました。";
      autoMessage[1] = "以下の値は無視されます。";
      for( int i = 2; i < autoMessage.length; i++ )
        autoMessage[i] = autoUnknowKeys.elementAt(i-2).toString();

      JOptionPane.showMessageDialog( null, autoMessage, "Infomation", JOptionPane.INFORMATION_MESSAGE );
    }
  }


  // Purpose   : Serialize this class
  // Argument  : BufferedWriter opening setting file
  // Return    : void
  public void serialize( BufferedWriter autoWriter ) throws IOException
  {
    // Write header
    autoWriter.write( "/*------ Host ------*/" );
    autoWriter.newLine();

    // Content
    String autoWriteContent = null;

    // Save host name
    autoWriteContent = "HostName\t\t\t" + HostName;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // save connection port
    autoWriteContent = "ConnectionPort\t\t\t" + ConnectionPort;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // save user name
    autoWriteContent = "UserName\t\t\t" + UserName;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // save password
    autoWriteContent = "Password\t\t\t" + this.enCrypting( Password );
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // save shot path
    autoWriteContent = "ShotPath\t\t\t" + ShotPath;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // sava server type
    autoWriter.write( "/*\tServer Type\t*/" );
    autoWriter.newLine();
    autoWriter.write( "/*\t1: FTP 2: Windows 3: Machintosh 4: local\t*/" );
    autoWriter.newLine();
    autoWriteContent = "ServerType\t\t\t" + ServerType;

    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // Write end section
    autoWriter.write( "/*------ End ------------*/" );
    autoWriter.newLine();
  }

  // Purpose   : deCrypting string
  // Argument  : Crypted string
  // Return    : decrypted string
  public String deCrypting( String autoCrypted )
  {
    if( autoCrypted.length() < 1 )
      return autoCrypted;

    StringBuffer autoBuffer = new StringBuffer();
    int autoDiv = 0;
    int autoMod = 0;
    char autoCharacter = 0;
    int autoSize = autoCrypted.length() / 4;
    for( int i = 0; i < autoSize; i++ )
    {
      autoDiv = Integer.parseInt( autoCrypted.substring( 4 * i, 4 * i + 2 ) );
      autoMod = Integer.parseInt( autoCrypted.substring( 4 * i + 2, 4 * i + 4 ) );
      autoCharacter = (char)(autoDiv * 13 + autoMod);
      autoBuffer.append( autoCharacter );
    }
    return autoBuffer.toString();
  }

  // Purpose   : deCrypting string
  // Argument  : Crypted string
  // Return    : decrypted string
  public String enCrypting( String autoCrypting )
  {
    if( autoCrypting.length() < 1 )
      return autoCrypting;

    StringBuffer autoCrypted = new StringBuffer();

    int autoDiv = 0;
    int autoMod = 0;

    byte[] autoBytes = Password.getBytes();
    for( int i = 0; i < autoBytes.length; i++ )
    {
      autoDiv = autoBytes[i] / 13;
      if( autoDiv < 10 )
        autoCrypted.append( "0" + autoDiv );
      else
        autoCrypted.append( autoDiv );

      autoMod = autoBytes[i] % 13;
      if( autoMod < 10 )
        autoCrypted.append( "0" + autoMod );
      else
        autoCrypted.append( autoMod );
    }
    return autoCrypted.toString();
  }

}