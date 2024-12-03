/*
    GAMMA Viewer - BasicConnectionMode : Super class for each implementation of mode in this application
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


    GAMMA Viewer - BasicConnectionMode : モードを実装するクラスに対する基本クラス
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

import gammaviewer.ViewerConstants;
import gammaviewer.InfomationException;
import gammaviewer.Data.*;
import gammaviewer.UI.LoginDialog;

import javax.swing.JOptionPane;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.net.*;
import java.util.*;


public abstract class BasicConnectionMode implements ViewerConstants, Runnable
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Network controller
  NetworkManager NetMgr = null;

  ViewerMode IdentMode = null;

  // flags to synchronize
  volatile boolean goFlag = true;

  // Class of connecte to server
  ServerConnection Session = null;

  // Host to connect
  Host ConnectHost = null;


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------


  public BasicConnectionMode( NetworkManager autoMgr, ViewerMode autoMode )
  {
    NetMgr = autoMgr;
    // Create new object
    IdentMode = new ViewerMode( autoMode.getMode() );
    // And copy
    IdentMode.copyContents( autoMode );

    try
    {
      // Get host to connect
      ConnectHost = new Host();
      ConnectHost.copyContents( NetMgr.getData().getHost( IdentMode.getHostToConnect() ) );
    }
    catch( InfomationException autoInfoExcpt )
    {
      Object[] autoMessage = { autoInfoExcpt.toString(),
                             "at BasicConnectionMode::BasicConnectionMode" };
      JOptionPane.showMessageDialog( null, autoMessage, "Error", JOptionPane.ERROR_MESSAGE );
    }

    // Get connection class
    Session = ConnectHost.getConnectClass();
    // Initialize flags
    goFlag = true;
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : Called when want to stop this thread
  // Argument  : void
  // Return    : void
  synchronized public void stopThread()
  {
    goFlag = false;
  }


  // Purpose   : To check this thread is wanted to stop or not.
  // Argument  : void
  // Return    : True if a user wants to stop downloading
  synchronized protected void isStopSignal() throws InterruptedException
  {
    if( !goFlag )
    {
      Session.logout();
      throw new InterruptedException();
    }
  }


  // Purpose   : Login
  // Argument  : void
  // Return    : void
  protected void login() throws UnknownHostException,
                                InfomationException,
                                InterruptedException
  {

    // Create session
    Session.CreateConnection( ConnectHost.getAddress(), ConnectHost.getConnectionPort() );
    isStopSignal();

    // Logging in to the host
    NetMgr.showMessage( "サーバ" + ConnectHost.getName() + "にログイン中" );
    boolean autoSuccess =
        Session.login( ConnectHost.getUserName(), ConnectHost.getPassword() );

    // If login failed
    if( !autoSuccess )
    {
      while( !autoSuccess )
      {
        LoginDialog autoDialog = NetMgr.getLoginDialog( ConnectHost );
        autoDialog.show();

        if( autoDialog.getAnswer() == LoginDialog.LD_OK )
        {
          // Save username and password
          ConnectHost.setUserName( autoDialog.getUserName() );
          ConnectHost.setPassword( autoDialog.getPassword() );
          Host autoHost = NetMgr.getData().getHost( ConnectHost.getName() );
          autoHost.setUserName( autoDialog.getUserName() );
          autoHost.setPassword( autoDialog.getPassword() );

          // Update server settings
          autoDialog.syncServerSettings( ConnectHost );

          // Retry login
          NetMgr.showMessage( "サーバ" + ConnectHost.getName() + "にログイン中" );
          autoSuccess = Session.login( autoDialog.getUserName(), autoDialog.getPassword() );

        }
        else // User clicked cancel button
          throw new InterruptedException();
      }
      isStopSignal();
    }

    // Logged on
    NetMgr.showMessage( "ログイン完了" );
  }


  // Purpose   : Logout
  // Argument  : void
  // Return    : void
  protected void logout()
  {
    Session.logout();
  }


  // Purpose   : To get one list of ports.
  // Argument  : void
  // Return    : Integer vector array, null if some error happen
  protected byte[] getPortDataAsByte( String autoPortPath ) throws InfomationException
  {
    try
    {
      // Get the file
      return Session.get( autoPortPath ).toByteArray();
    }
    catch( FileNotFoundException autoFileExcpt )
    {
      return new byte[0];
    }
  }
  protected String getPortDataAsString( String autoPortPath ) throws InfomationException
  {
    try
    {
      // Get the file
      return Session.get( autoPortPath ).toString();
    }
    catch( FileNotFoundException autoFileExcpt )
    {
      return "";
    }
  }


  // Purpose   : Get download file list
  // Argument  : Shot number and port list
  // Return    : file name like "SHOT#/"
  public Vector getDownloadFileList( Vector autoShotList, Vector autoPortList ) throws InfomationException
  {
    // Cache manager
    CacheManager autoCacheMgr = NetMgr.getCacheManager();

    int autoShotListSize = autoShotList.size();
    int autoPortListSize = autoPortList.size();

    // Hashtable
    Hashtable autoTable = new Hashtable( autoShotListSize * autoPortListSize );

    // File list that should be downloaded.
    Vector autoFileList = new Vector( autoShotListSize * autoPortListSize );

    // Sbot number and port name
    String autoShotNumber = null;
    String autoPortName   = null;

    // Create file list
    int i = 0, j = 0;
    for( i = 0; i < autoShotListSize; i++ )
    {
      // Get shot number
      autoShotNumber = autoShotList.elementAt(i).toString();
      for( j = 0; j < autoPortListSize; j++ )
      {
        // Get port name
        autoPortName = autoPortList.elementAt(j).toString();

        // If cache file do not exist
        if( ! autoCacheMgr.cacheExists( autoShotNumber, autoPortName ) )
        {
          // Get file list to have to download
          PortInfomation autoPortInfo = this.NetMgr.getData().getPortInfo( autoPortName );
          Vector autoPortFileList     = autoPortInfo.getDownloadFileList();

          for( int k = 0; k < autoPortFileList.size(); k++ )
          {
            String autoFileKey = autoShotNumber + "/" + autoPortFileList.elementAt(k).toString();
            // If this file is still not added
            if( ! autoTable.containsKey( autoFileKey ) )
            {
              // Add download file list
              autoFileList.addElement( autoFileKey );
              // And add table
              autoTable.put( autoFileKey, autoFileKey );
            }
          }
        }
      }
    }
    return autoFileList;
  }


  // Purpose   : To get a list of files
  // Argument  : void
  // Return    : Integer vector array, null if some error happen
  public Hashtable downloadFile( Vector autoFileList ) throws InfomationException, InterruptedException
  {
    // File array that downloaded
    Hashtable autoDownloadedFiles = new Hashtable( autoFileList.size() );

    final String autoShotPath = this.ConnectHost.getShotPath() + "/";
    String autoFileName       = null;
    String autoFilePath       = null;

    // Create file list
    int i = 0;
    for( i = 0; i < autoFileList.size(); i++ )
    {
      // Get file name like "SHOT#/PORT"
      autoFileName = autoFileList.elementAt(i).toString();

      // Start downloading
      NetMgr.showMessage( autoFileName + "をダウンロードしています" );
      autoDownloadedFiles.put( autoFileName, this.getPortDataAsByte( autoShotPath + autoFileName ) );
      isStopSignal();
    }
    return autoDownloadedFiles;
  }


  // Purpose   : Get port datas
  // Argument  : void
  // Return    : Vector of PortData class
  public Vector getPortDatas( Vector autoShotList, Vector autoPortList )
                     throws UnknownHostException, InfomationException, InterruptedException
  {
    // Get download file list
    Vector autoFileList = this.getDownloadFileList( autoShotList, autoPortList );

    // Hashtable of downloaded files
    Hashtable autoDownloadedFiles = null;

    // If there are files to have to download
    if( autoFileList.size() > 0 )
    {
      try
      {
        // Login
        this.login();
        // get files
        autoDownloadedFiles = this.downloadFile( autoFileList );
        // Logout
        this.logout();
      }
      // If throw interruptedException
      catch( InterruptedException autoItrptExcpt )
      {
        this.logout();
        throw autoItrptExcpt;
      }
    }

    // Port data array
    Vector autoDataArray = new Vector();

    // Shot number and port name.
    String autoShot = null;
    String autoPort = null;

    // Cache manager
    CacheManager autoCacheMgr = this.NetMgr.getCacheManager();

    int i = 0, j = 0;
    for( i = 0; i < autoShotList.size(); i++ )
    {
      autoShot = autoShotList.elementAt( i ).toString();
      for( j = 0; j < autoPortList.size(); j++ )
      {
        autoPort = autoPortList.elementAt( j ).toString();
        // If there is no cache file.
        if( ! autoCacheMgr.cacheExists( autoShot, autoPort ) )
        {
          PortInfomation autoPortInfo = this.NetMgr.getData().getPortInfo( autoPort );
          PortData autoData = new PortData( autoShot, autoPortInfo );
          autoData.setData( autoDownloadedFiles );

          // If user want to cache, Cache a port data
          if( this.IdentMode.doCache() )
          {
            this.NetMgr.showMessage( "キャッシュしています.." );
            autoCacheMgr.storeToCache( autoData );
          }

          // Add data
          autoDataArray.addElement( autoData );
        }
        else // If exists a cache file
        {
          this.NetMgr.showMessage( "キャッシュを読んでいます.." );
          autoDataArray.addElement( autoCacheMgr.readCache( autoShot, autoPort ) );
        }
      }
    }
    return autoDataArray;
  }

}
