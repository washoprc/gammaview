/*
    GAMMA Viewer - ViewerDatas : Data class containing all datas in this application
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


    GAMMA Viewer - ViewerDatas : 全てのデータをカプセルしたクラス
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

import gammaviewer.InfomationException;
import gammaviewer.ViewerConstants;
import gammaviewer.Data.Filter.*;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.io.*;
import java.util.*;


public class ViewerDatas implements ViewerConstants
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  /* For ShotController */
  ViewerMode AutoUpdater = new ViewerMode( MODE_AUTOUPDATE );
  ViewerMode ShotChooser = new ViewerMode( MODE_CHOOSESHOT );
  ViewerMode CurrentMode = ShotChooser;


  /* For DetailSettings */

  // Servers
  // Key ; server name   Value ; class HostInfomation
  Hashtable ServerList = new Hashtable();

  /* Data about PortSettings */

  // Port list
  // Key ; port name  Value ; class Port
  Hashtable RegisteredPorts = new Hashtable();


  /* Downloaded data */

  // Key ; shot number and port name   Value ; Class Portdata
  Vector ShotData = new Vector();

  /* Cache management */

  // Cache Manager
  CacheManager CacheMgr = null;

  /* Filter management */

  // Filter manager
  FilterLoader FilterLdr = null;

  // Home directory
  File HomeDirectory = null;

  // Color settings
  Color FrameColor = Color.black;
  Color CrossColor = Color.blue;
  Color ValueColor = Color.black;
  Color DataColor  = Color.black;


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

  public ViewerDatas()
  {
    CacheMgr = new CacheManager( this );
    FilterLdr = new FilterLoader();

    // Determine home directory.
    // Check whether we have write permittion to running and user dirctory.
    // Or, Exist setting files.

    // Pick up 2 candidate of home dirctory
    File autoRunDir = new File( this.CURRENTDIR );
    File autoUserDir = new File( this.HOME );

    // Create File object of settig file
    File autoRunDirSetting = new File( autoRunDir, "gvSetting.txt" );
    File autoUserDirSetting = new File( autoUserDir, "gvSetting.txt" );

    if( autoRunDirSetting.exists() )
      HomeDirectory = autoRunDir;

    else if( autoUserDirSetting.exists() )
      HomeDirectory = autoUserDir;

    else if( autoRunDir.canWrite() )
      HomeDirectory = autoRunDir;

    else if( autoUserDir.canWrite() )
      HomeDirectory = autoUserDir;

    else
      HomeDirectory = new File( System.getProperty( "java.io.tmpdir" ) );

    // Read setting file and port infomation file
    this.serialize( SERIALIZE_MODE_READ );
  }


  // Purpose   : finalization
  // Argument  : void
  // Return    : void
  public void finalize()
  {
    this.serialize( SERIALIZE_MODE_WRITE );
    CacheMgr.finalize();
  }


  //-----------------------------------------
  /*  Serialization  */
  //-----------------------------------------


  // Purpose   : Serialization
  // Argument  : void
  // Return    : void
  public void serialize( int autoSerialMode )
  {
    try
    {
      File autoSettingFile = new File( HomeDirectory, "gvSetting.txt" );
      File autoPortDataFile = new File( HomeDirectory, "gvPort.txt" );

      if( autoSerialMode == SERIALIZE_MODE_WRITE )
      {
        // If gvSetting.txt is exist and it can be written,
        //   or gvSetting.txt is not exist but this directory can be wrtten,
        //   then write out.
        if( autoSettingFile.canWrite() ||
         ( !autoSettingFile.exists() && HomeDirectory.canWrite() ) )
        {
          BufferedWriter autoWriter = new BufferedWriter( new FileWriter( autoSettingFile ) );

          /* Write data */

          // About AutoUpdate mode
          AutoUpdater.serialize( autoWriter );
          // About ChooseShot mode
          ShotChooser.serialize( autoWriter );

          // About host
          for( int i = 0; i < ServerList.size(); i++ )
            this.getHost(i).serialize( autoWriter );

          // About cache
          CacheMgr.serialize( autoWriter );

          // About color
          this.serializeColor( autoWriter, "/*------ FrameColor  ------*/", FrameColor );
          this.serializeColor( autoWriter, "/*------ CorssColor  ------*/", CrossColor );
          this.serializeColor( autoWriter, "/*------ DataColor  ------*/",  DataColor );
          this.serializeColor( autoWriter, "/*------ ValueColor  ------*/", ValueColor );

          autoWriter.flush();
          autoWriter.close();
        }
        // About ports
        // If gvPort.txt is exist and can write
        // ( already known this directory can write! )
        if( autoPortDataFile.canWrite() ||
         ( !autoPortDataFile.exists() && HomeDirectory.canWrite() ) )
        {
          BufferedWriter autoWriter = new BufferedWriter( new FileWriter( autoPortDataFile ) );

          for( int i = 0; i < RegisteredPorts.size(); i++ )
            this.getPortInfo(i).serialize( autoWriter );

          autoWriter.flush();
          autoWriter.close();
        }
      }
      else // autoSerialMode == SERIALIZE_MODE_READ
      {
        if( autoSettingFile.canRead() )
        {
          BufferedReader autoReader = new BufferedReader( new FileReader( autoSettingFile ) );

          /* Read data */

          // File contents
          Vector autoContents = getSection( autoReader );
          // If there are unrecognize section
          boolean autoSomeParameterCannotRead = false;

          while( autoContents != null )
          {
            if( autoContents.elementAt(0).equals( "/*------ AutoUpdater  ------*/" ) )
              AutoUpdater.unserialize( autoContents );

            else if( autoContents.elementAt(0).equals( "/*------ ShotChooser  ------*/" ) )
              ShotChooser.unserialize( autoContents );

            else if( autoContents.elementAt(0).equals( "/*------ Host ------*/" ) )
            {
              Host autoHost = new Host();
              autoHost.unserialize( autoContents );
              ServerList.put( autoHost.getName(), autoHost );
            }

            else if( autoContents.elementAt(0).equals( "/*------ Caches       ------*/" ) )
              CacheMgr.unserialize( autoContents );

            else if( autoContents.elementAt(0).equals( "/*------ FrameColor  ------*/" ) )
              FrameColor = this.unserializeColor( autoContents, FrameColor );
            else if( autoContents.elementAt(0).equals( "/*------ CorssColor  ------*/" ) )
              CrossColor = this.unserializeColor( autoContents, CrossColor );
            else if( autoContents.elementAt(0).equals( "/*------ DataColor  ------*/" ) )
              DataColor = this.unserializeColor( autoContents, DataColor );
            else if( autoContents.elementAt(0).equals( "/*------ ValueColor  ------*/" ) )
              ValueColor = this.unserializeColor( autoContents, ValueColor );

            else
              autoSomeParameterCannotRead = true;

            autoContents = getSection( autoReader );
          }
        }
        else // Initializa default
          this.initSettings();

        // About ports
        if( autoPortDataFile.canRead() )
        {
          BufferedReader autoReader = new BufferedReader( new FileReader( autoPortDataFile ) );

          // File contents
          Vector autoContents = getSection( autoReader );
          // If there are unrecognize section
          boolean autoSomeParameterCannotRead = false;

          while( autoContents != null )
          {
            if( autoContents.elementAt(0).equals( "/*------ Port ------*/" ) )
            {
              PortInfomation autoPort = new PortInfomation();
              autoPort.unserialize( autoContents, FilterLdr );
              RegisteredPorts.put( autoPort.getPortName(), autoPort );
            }

            else
              autoSomeParameterCannotRead = true;

            autoContents = getSection( autoReader );
          }
        } // if autoPortFile.canRead()
        else
         this.initPorts();

      } // else ( serialization mode )
    } // try
    catch( InfomationException autoInfoExcpt )
    {
    }
    catch( IOException autoIOExcpt )
    {
    }
  }

  // Purpose   : Used to get section contents of setting files
  // Argument  : Reader of file
  // Return    : Vector that element is a line of file
  private Vector getSection( BufferedReader autoReader ) throws IOException
  {
    // File content
    String autoContent = autoReader.readLine();

    // If already arrive EOF
    if( autoContent == null )
      return null;

    // Setion buffer
    Vector autoBuffer = new Vector();

    // add contents while not EOF or end of section
    while( autoContent != null && !autoContent.startsWith( "/*------ End ------------*/" ) )
    {
      if( autoContent.length() > 0 )
        autoBuffer.addElement( autoContent );
      autoContent = autoReader.readLine();
    }

    return autoBuffer;
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
  public Color unserializeColor( Vector autoContents, Color autoColor ) throws IOException
  {
    // String that read
    String autoContent = null;
    // Key and value
    String autoKey = null;
    String autoValue = null;

    // Unknown parameters
    boolean autoUnknownParameter = false;
    Vector autoUnknowKeys = new Vector( 2, 2 );

    int autoRed   = autoColor.getRed();
    int autoBlue  = autoColor.getBlue();
    int autoGreen = autoColor.getGreen();

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
        // Get kay and value
        autoKey = autoContent.substring( 0, autoContent.indexOf( "\t" ) );
        autoValue = autoContent.substring( autoContent.lastIndexOf( "\t" ) + 1 );

        // Shot number
        if( autoKey.equals( "Red" ) )
        {
          autoRed = Integer.parseInt( autoValue );
          if( autoRed < 0 )            autoRed = 0;
          else if( autoRed > 255 )     autoRed = 255;
        }

        else if( autoKey.equals( "Blue" ) )
        {
          autoBlue = Integer.parseInt( autoValue );
          if( autoBlue < 0 )           autoBlue = 0;
          else if( autoBlue > 255 )    autoBlue = 255;
        }

        else if( autoKey.equals( "Green" ) )
        {
          autoGreen = Integer.parseInt( autoValue );
          if( autoGreen < 0 )           autoGreen = 0;
          else if( autoGreen > 255 )    autoGreen = 255;
        }

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
      autoMessage[0] = "設定ファイル中でサイズの読み込み時に不明なパラメータを見つけました。";
      autoMessage[1] = "以下の値は無視されます。";
      for( int i = 2; i < autoMessage.length; i++ )
        autoMessage[i] = autoUnknowKeys.elementAt(i-2).toString();

      JOptionPane.showMessageDialog( null, autoMessage, "Infomation", JOptionPane.INFORMATION_MESSAGE );
    }

    // return color object that is serialized
    return new Color( autoRed, autoGreen, autoBlue );
  }


  // Purpose   : Serialize this class
  // Argument  : BufferedWriter opening setting file
  // Return    : void
  // NOTE      : File format is below
  //
  //             /*--------- xxxx -------------*/          header
  //             kay      value                            key, tab, tab, tab, value
  //                ...
  //             kay      value                            key, tab, tab, tab, value
  //             /*------ End ------------*/               footer
  //
  private void serializeColor( BufferedWriter autoWriter,
                               String autoTitle,
                               Color autoColor ) throws IOException
  {
    // Write header
    autoWriter.write( autoTitle );
    autoWriter.newLine();

    String autoWriteContent = null;

    // Save positions
    autoWriteContent = "Red\t\t\t"   + autoColor.getRed();
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriteContent = "Blue\t\t\t"  + autoColor.getBlue();
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriteContent = "Green\t\t\t" + autoColor.getGreen();
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriter.write( "/*------ End ------------*/" );
    autoWriter.newLine();
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : Get home directory
  // Argument  : void
  // Return    : File object to home directory
  public File getHomeDir()  { return HomeDirectory; }


  // Purpose   : Get class ViewerMode
  // Argument  : integer meant either mode.
  // Return    : ViewerMode
  public ViewerMode getMode( int autoIntMode )
  {
    if( autoIntMode == MODE_AUTOUPDATE )     return AutoUpdater;
    else                                     return ShotChooser;
  }
  public ViewerMode getCurrentMode()    { return CurrentMode; }


  // Purpose   : Returns vector listing servers
  // Argument  : void
  // Return    : Vectorlize server list
  public Vector getHostList()
  {
    Vector autoList = new Vector();

    // Get keys
    for( Enumeration autoKeys = ServerList.keys(); autoKeys.hasMoreElements(); )
      autoList.addElement( autoKeys.nextElement() );

    // Return sorted vector
    return ViewerDatas.quickSort( autoList );
  }


  // Purpose   : To get server infomation
  // Argument  : Server name that wanted to know
  // Return    : Class host that name is shown in argument
  public Host getHost( Object autoName ) throws InfomationException
  {
    Host autoHost = (Host)ServerList.get( autoName );

    if( autoHost == null )
      throw new InfomationException( "指定されたサーバが見つかりませんでした。" );
    else
      return autoHost;
  }
  public Host getHost( int autoIndex ) throws InfomationException
  {
    // Check size
    if( autoIndex > ServerList.size() )
      throw new InfomationException( "内部エラーです。サーバの数より大きい値が指定されました。" );

    Enumeration autoElements = ServerList.elements();
    for( int i = 0; i < autoIndex; i++ )
      autoElements.nextElement();

    return (Host)autoElements.nextElement();
  }


  // Purpose   : Return cache path.
  // Argument  : void
  // Return    : Path to cache directory
  public String getCachePath()      { return CacheMgr.getCachePath(); }


  // Purpose   : Return max cache file amount
  // Argument  : void
  // Return    : Integer typed file amount able to cache
  public int getMaxCacheFileAmount()    { return CacheMgr.getMaxFileAmount(); }


  // Purpose   : Return graphics colors
  // Argument  : void
  // Return    : Color object of each graphics attributes
  public Color getFrameColor()      { return FrameColor; }
  public Color getCrossColor()      { return CrossColor; }
  public Color getValueColor()      { return ValueColor; }
  public Color getDataColor()       { return DataColor; }


  // Purpose   : Get registered port's infomation
  // Argument  : void
  // Return    : Vector of registered ports names
  public Vector getRegisteredPorts()
  {
    Vector autoList = new Vector();
    // Get keys
    for( Enumeration autoKeys = RegisteredPorts.keys(); autoKeys.hasMoreElements(); )
      autoList.addElement( autoKeys.nextElement() );
    return ViewerDatas.quickSort( autoList );
  }


  // Purpose   : Get port's infomation
  // Argument  : name of port
  // Return    : Selected port
  public PortInfomation getPortInfo( Object autoPort ) throws InfomationException
  {
    PortInfomation autoPortInfo = (PortInfomation)RegisteredPorts.get( autoPort );

    if( autoPortInfo == null )
      throw new InfomationException( "指定されたポートが見つかりませんでした。" );
    else
      return autoPortInfo;
  }
  public PortInfomation getPortInfo( int autoIndex ) throws InfomationException
  {
    // Check size
    if( autoIndex > RegisteredPorts.size() )
      throw new InfomationException( "内部エラーです。ポートの数より大きい値が指定されました。" );

    Enumeration autoElements = RegisteredPorts.elements();
    for( int i = 0; i < autoIndex; i++ )
      autoElements.nextElement();

    return (PortInfomation)autoElements.nextElement();
  }

  // Purpose   : Get the referrence of shotdatas
  // Argument  : void
  // Return    : Vector elemented Double typed datas.
  public Vector getShotData()         { return ShotData; }


  // Purpose   : Returns cache manager itself
  // Argument  : void
  // Return    : Cache manager
  public CacheManager getCacheManager() { return CacheMgr; }


  // Purpose   : Returns cache manager itself
  // Argument  : void
  // Return    : Cache manager
  public FilterLoader getFilterLoader() { return FilterLdr; }


  // Purpose   : Register current mode is to argument
  // Argument  : Current mode
  // Return    : void
  public void setCurrrentMode( ViewerMode m )   { CurrentMode = m; }


  // Purpose   : To change cache path
  // Argument  : New directory path to cache
  // Return    : void
  public void setCachePath( String autoNewPath )
  {
    CacheMgr.changedCacheDir( autoNewPath );
  }


  // Purpose   : To change max cache file amount
  // Argument  : New amount of file cache
  // Return    : void
  public void setMaxCacheSize( int autoMewAmount ) throws InfomationException
  { CacheMgr.setMaxFileAmount( autoMewAmount ); }


  // Purpose   : Set graphics colors
  // Argument  : Color object of each graphics attributes
  // Return    : void
  public void setFrameColor( Color autoNewColor )      { FrameColor = autoNewColor; }
  public void setCrossColor( Color autoNewColor )      { CrossColor = autoNewColor; }
  public void setValueColor( Color autoNewColor )      { ValueColor = autoNewColor; }
  public void setDataColor( Color autoNewColor )       { DataColor = autoNewColor; }


  // Purpose   : Set port datas
  // Argument  : Vector of class PortData
  // Return    : void
  public void setShotData( Vector v ) { ShotData = v; }


  // Purpose   : Add server to ServerList
  // Argument  : Class Host
  // Return    : void
  public void addServer( Host autoHost ) throws InfomationException
  {
    Host autoListedHost = (Host)ServerList.get( autoHost.getName() );
    // If not registered.
    if( autoListedHost == null )
      ServerList.put( autoHost.getName(), autoHost );
    // If already registered.
    else
      throw new InfomationException("指定したホスト名はすでに使用されています。別の名前を指定して下さい。");
  }


  // Purpose   : Delete server from ServerList
  // Argument  : Class Host
  // Return    : void
  public void delServer( Object autoHost ) throws InfomationException
  {
    if( ServerList.size() <= 1 )
      throw new InfomationException( "サーバは全て削除できません。新しいサーバを設定してから削除してください。" );
    else
      ServerList.remove( autoHost );
  }


  // Purpose   : Replace server from ServerList
  // Argument  : Class Host
  // Return    : void
  public void replaceServer( Host autoHost, Object autoOldKey )
  {
    ServerList.remove( autoOldKey );
    ServerList.put( autoHost.getName(), autoHost );
  }


  // Purpose   : Add port to port list
  // Argument  : Class Host
  // Return    : void
  public void addPort( PortInfomation autoPort ) throws InfomationException
  {
    PortInfomation autoListedPort = (PortInfomation)RegisteredPorts.get( autoPort.getPortName() );
    // If not registered.
    if( autoListedPort == null )
      RegisteredPorts.put( autoPort.getPortName(), autoPort );
    // If already registered.
    else
      throw new InfomationException("指定したポート名はすでに使用されています。別の名前を指定して下さい。");
  }


  // Purpose   : Delete port from port list
  // Argument  : Class Host
  // Return    : void
  public void delPort( Object autoPort ) throws InfomationException
  {
    if( RegisteredPorts.size() <= 1 )
      throw new InfomationException( "ポートは全て削除できません。" );
    RegisteredPorts.remove( autoPort );
  }


  // Purpose   : Replace port from port list
  // Argument  : Class Host
  // Return    : void
  public void replacePort( PortInfomation autoPort, Object autoOldKey )
  {
    RegisteredPorts.remove( autoOldKey );
    RegisteredPorts.put( autoPort.getPortName(), autoPort );
  }


  // Purpose   : Data Initialization
  // Argument  : void
  // Return    : void
  private void initSettings()
  {
    Object[] autoMessage = { "始めに利用許諾契約書（ライセンス）をお読みください。",
                             "本プログラムを使用された場合、同意したものとします。" };
    int autoAnswer = JOptionPane.showConfirmDialog( null, autoMessage, "利用許諾契約",
                         JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE );

    if( autoAnswer == JOptionPane.NO_OPTION )
      System.exit( 0 );

    try
    {
      // add tonegawa and kasumi
      // Add kasumi(default)
      Host autoHost = new Host();
      ServerList.put( autoHost.getName(), autoHost );
      // Add tonegawa
      autoHost = new Host();
      autoHost.setHost( "tonegawa.prc.tsukuba.ac.jp" );
      autoHost.setShotPath( "/usr9/shot" );
      ServerList.put( autoHost.getName(), autoHost );

      // Init CachePass
      File autoCacheDir = new File( HomeDirectory, "gvCache" );
      CacheMgr.changedCacheDir( autoCacheDir.getPath() );
    }
    catch( InfomationException autoInfoExcpt )
    {
      JOptionPane.showMessageDialog( null, autoInfoExcpt.toString(), "Error", JOptionPane.ERROR_MESSAGE );
    }
  }


  // Purpose   : Data Initialization
  // Argument  : void
  // Return    : void
  private void initPorts()
  {
    // RF
    RegistratePort( "DMCC", "DMC500,DMC600"     , 100,  0, 5, -5, 1,  0, 50, 250, "Diamag Filter" );
    RegistratePort( "DMCW", "DMC100,DMC200"     , 100,  0, 2.5, -2.5, 1,  0, 50, 250, "Diamag Filter" );
    RegistratePort( "RF1East", "PA2I00"         , 200, 30,  5, -5, 5,  0, 50, 250, "NormalFilter(16bit)" );
    RegistratePort( "RF2", "PA3I00"             , 200, 30,  5, -5, 5,  0, 50, 250, "NormalFilter(16bit)" );
    RegistratePort( "RF3", "LMM100"             , 200, 30,  5, -5, 5,  0, 50, 250, "NormalFilter(16bit)" );

    // Spects
    RegistratePort( "Ha_C52", "HA8800"          ,  50, 30, 5, -5, -5, 5, 50, 250, "NormalFilter(16bit)" );
    RegistratePort( "Ha_C01", "HAZ100"          , 200, 50, 5, -5, -5, 5, 50, 250, "NormalFilter(12bit)" );
    RegistratePort( "Ha_EA" , "HAZ400"          , 200, 50, 5, -5, -5, 5, 50, 250, "NormalFilter(12bit)" );
    RegistratePort( "Ha_EB" , "PG1100"          , 200, 50, 5, -5, -5, 5, 50, 250, "NormalFilter(12bit)" );

    // ELA
    RegistratePort( "ELA West", "ELWM00"        , 200, 50, 5, -5, 5, -5, 50, 250, "NormalFilter(12bit)" );
    RegistratePort( "ELA East", "ELEM00"        , 200, 50, 5, -5, 5, -5, 50, 250, "NormalFilter(12bit)" );

    // ECRH
    RegistratePort( "LEE EastI", "LEE100",  20, 50, 5, -5, 5, -5, 50, 250, "NormalFilter(12bit)" );
    RegistratePort( "LEE WestI", "LEE200",  20, 50, 5, -5, 5, -5, 50, 250, "NormalFilter(12bit)" );
    RegistratePort( "LEE EastV", "LEE300",  20, 50, 5, -5, 5, -5, 50, 250, "NormalFilter(12bit)" );
    RegistratePort( "LEE WestV", "LEE400",  20, 50, 5, -5, 5, -5, 50, 250, "NormalFilter(12bit)" );
    RegistratePort( "ISP CC I" , "ISC100",  20,100, 5, -5, 5, -5, 50, 250, "NormalFilter(12bit)" );
    RegistratePort( "ISP CC V" , "ISC200",  20,100, 5, -5, 5, -5, 50, 250, "NormalFilter(12bit)" );

    // Microwave
    RegistratePort( "NLCC", "MIAA01,MJC100,MJC200",  25, 50, 5, -5, 10, 0, 50, 250, "NL Filter" );
    RegistratePort( "NLEA", "MJEA00,MJEZ00"       ,  40, 50, 5, -5, 10, 0, 50, 250, "NL Filter" );
    RegistratePort( "NLEB", "MIAA00"              ,  25, 50, 5, -5, 10, 0, 50, 250, "NL Filter" );
    RegistratePort( "NLEP", "MJEP00,MJEQ00"       ,  40, 50, 5, -5, 10, 0, 50, 250, "NL Filter" );
    RegistratePort( "NLWA", "MJWA00,MJWA01"       ,  40, 50, 5, -5, 10, 0, 50, 250, "NL Filter" );
    RegistratePort( "NLWB", "MJWB00,MJWB01"       ,  40, 50, 5, -5, 10, 0, 50, 250, "NL Filter" );
    RegistratePort( "NLWP", "NLW100,NLW200"       ,  40, 50, 5, -5, 10, 0, 50, 250, "NL Filter" );

    // BP
/*
    RegistratePort( "BPCCPotential" , "BE3100,BE3200,BE3300,BE3400,BE3500,BE3600,BE3700,BE3800,BE3900,BE3A00,BE3B00,BE3C00,BE6100,BE6200,BE6300,BE6400,BE6500,BE6600,BE6700,BE6800,BE6900,BE6A00,BE6B00,BE6C00"
                                     , 32,   50, 5, -5, 600, 0, 50, 250, "BPCC Filter" );
    RegistratePort( "BPEBPotential" , "BE5100,BE5200,BE5300,BE5400,BE5500,BE5600,BE5700,BE5800,BE5900,BE5A00,BE5B00,BE5C00,BE6100,BE6200,BE6300,BE6400,BE6500,BE6600,BE6700,BE6800,BE6900,BE6A00,BE6B00,BE6C00"
                                     , 32,   50, 5, -5, 500, -100, 50, 250, "BPEB Filter" );
    RegistratePort( "ELECAPotential", "BE1100,BE1200,BE1300,BE1400,BE1500,BE1600,BE1700,BE1800,BE1900,BE1A00,BE1B00,BE1C00,BE2100,BE2200,BE2300,BE2400,BE2500,BE2600,BE2700,BE2800,BE2900,BE2A00,BE2B00,BE2C00,BE4100,BE4200,BE4300,BE4400,BE4500,BE4600,BE4700,BE4800,BE4900,BE4A00,BE4B00,BE4C00"
                                     , 2500, 55, 5, -5, 800, 0, 50, 250, "ELECA Filter" );
*/
  }

  private void RegistratePort( String autoPortName,
                               String autoRelated,
                               double autoSampling,
                               double autoTrigger,
                               double autoMax,
                               double autoMin,
                               double autoDrawMax,
                               double autoDrawMin,
                               double autoStart,
                               double autoEnd,
                               String autoFilterName )
  {
    try
    {
      PortInfomation autoPort = new PortInfomation();
      autoPort.setPortName    ( autoPortName );
      autoPort.setRelatedPorts( autoRelated );
      autoPort.setSamplingTime( autoSampling );
      autoPort.setTriggerTime ( autoTrigger );
      autoPort.setRange       ( autoMax, autoMin );
      autoPort.setDrawRange   ( autoDrawMax, autoDrawMin );
      autoPort.setTimeRange   ( autoStart, autoEnd );
      autoPort.setFilter      ( FilterLdr.getFilter( autoFilterName ) );

      // Registration
      RegisteredPorts.put( autoPort.getPortName(), autoPort );
    }
    catch( InfomationException autoInfoExcpt )
    {
    }
  }


  // Purpose   : Quick sort implementation
  // Argument  : Vector wanted to sort
  // Return    : Sorted vector
  static public Vector quickSort( Vector autoData )
  {
    ViewerDatas.quickSort( autoData, 0, autoData.size() - 1 );
    return autoData;
  }
  static public void quickSort( Vector autoData, int autoStart, int autoEnd )
  {
    if ( autoStart < autoEnd )
    {
      // 対象の中央にあるものを基準値として選定
      int autoCenterIndex = ( autoStart + autoEnd ) / 2;
      // 基準値の設定
      String autoCenter   = autoData.elementAt( autoCenterIndex ).toString();
      // 基準値を選んだ場所に一番左の要素を入れる
      autoData.setElementAt( autoData.elementAt( autoStart ), autoCenterIndex );
      int autoPos = autoStart;
      for( int i = autoStart + 1; i <= autoEnd; i++ )
      {
        // 配列autoData[i]が基準値より小さいければ，
        if( autoData.elementAt( i ).toString().compareTo( autoCenter ) < 0 )
        {
          // 値を入れる配列インデックスpを計算し，
          autoPos += 1;
          // a[p]とa[i]を交換する．
          Object autoTmp = autoData.elementAt( autoPos );
          autoData.setElementAt( autoData.elementAt( i ), autoPos );
          autoData.setElementAt( autoTmp, i );
        }
      }
      // autoData[autoStart+1]からautoData[autoPos]までは基準値より小さい．
      autoData.setElementAt( autoData.elementAt( autoPos ), autoStart );
      // autoData[autoStart]にautoData[autoPos]を代入
      autoData.setElementAt( autoCenter, autoPos );
      // 基準値をa[p]に入れる．
      /* これでautoData[autoStart]からautoData[autoPos-1]はautoData[autoPos]未満，
      autoData[autoPos+1]からautoData[autoEnd]はautoData[autoPos]以上となる．*/

      ViewerDatas.quickSort( autoData, autoStart, autoPos-1 );
      // 分割したものにクイックソートを適用
      ViewerDatas.quickSort( autoData, autoPos + 1, autoEnd );
      // 分割したものにクイックソートを適用
    }
  }
}

