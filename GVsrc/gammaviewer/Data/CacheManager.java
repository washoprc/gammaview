/*
    GAMMA Viewer - CacheManager : Management class of cache
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


    GAMMA Viewer - CacheManager : キャッシュ管理用クラス
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

import gammaviewer.*;

import javax.swing.JOptionPane;
import java.text.DecimalFormat;
import java.io.*;
import java.util.*;


public class CacheManager implements ViewerConstants
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  // Refference to ViewerDatas
  ViewerDatas Data = null;

  // File object to cache dir
  String CachePath = "";
  int MaxCacheFileAmount = 200;

  // True if can not write in cache dir
  boolean NotExist = false;
  boolean ReadOnly = false;

  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

  public CacheManager( ViewerDatas autoData )
  {
    Data = autoData;
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------

  // Purpose   : Initialization
  // Argument  : void
  // Return    : void
  public void init()
  {
  }


  // Purpose   : To clean up
  // Argument  : void
  // Return    : void
  public void finalize()
  {
    // Cache file management
    this.cacheManagement();
  }



  // Purpose   : To get current cache path
  // Argument  : void
  // Return    : String of cache path
  public String getCachePath()  { return CachePath; }


  // Purpose   : To get max file amount of cache
  // Argument  : void
  // Return    : The amount of cache
  public int getMaxFileAmount()  { return MaxCacheFileAmount; }


  // Purpose   : To get max file amount of cache
  // Argument  : New file amount
  // Return    : void
  public void setMaxFileAmount( int autoNewAmount ) throws InfomationException
  {
    if( autoNewAmount <= -2 )
      throw new InfomationException( "キャッシュファイル数に-2以下の負の値が設定されています。" );
    else
      MaxCacheFileAmount = autoNewAmount;
  }


  // Purpose   : Check cache file is exist or not
  // Argument  : Shot number and port name
  // Return    : true if cache exists, false if not
  public boolean cacheExists( String autoShotNumber, String autoPort )
  {
    String autoCacheFilePath = CachePath + File.separator
                             + autoShotNumber + "-" + autoPort + ".txt";
    File autoCacheFile = new File( autoCacheFilePath );
    return autoCacheFile.exists();
  }


  // Purpose   : Cache directory file amount management
  // Argument  : void
  // Return    : void
  public void changedCacheDir( String autoNewCacheDirPath )
  {
    if( autoNewCacheDirPath.equals( CachePath ) )
      return;

    else
    {
      File autoNewCacheDir = new File( autoNewCacheDirPath );
      if( !autoNewCacheDir.exists() )
      {
        // If can not create new cache dir
        if( ! new File( autoNewCacheDir.getParent() ).canWrite() )
          NotExist = true;
        else
          NotExist = false;

        Object[] autoMessage = {"キャッシュディレクトリ：" + autoNewCacheDir.getPath(),
                                "が存在しません。作成します。" };
        JOptionPane.showMessageDialog( null, autoMessage, "ディレクトリ作成", JOptionPane.INFORMATION_MESSAGE );
        autoNewCacheDir.mkdirs();
      }
      else // If cache dir is exist, but can not write
      {
        if( ! autoNewCacheDir.canWrite() )
          ReadOnly = true;
        else
          ReadOnly = false;
      }

      // If exist old caching path
      if( !CachePath.equals( "" ) )
      {
        File autoCacheDir = new File( CachePath );

        Object[] autoMessage = {"古いキャッシュディレクトリ：" + autoCacheDir.getPath(),
                                "を削除しますか？" };
        int autoAnswer = JOptionPane.showConfirmDialog( null, autoMessage, "削除",
                         JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE );

        if( autoAnswer == JOptionPane.YES_OPTION )
        {
          String[] autoDeleteFiles = autoCacheDir.list();
          for( int i = 0; i < autoDeleteFiles.length; i++ )
            new File( autoCacheDir, autoDeleteFiles[i] ).delete();
          autoCacheDir.delete();
        }
      }
      // Change cache path
      CachePath = autoNewCacheDirPath;
    }
  }


  // purpase   : Read cached data
  // Argument  : Shot number and port name
  // Return    : Class PortData read from cache
  public PortData readCache( String autoShotNumber, String autoPort )
  {
    BufferedReader autoReader =null;
    try
    {
      PortInfomation autoPortInfo = Data.getPortInfo( autoPort );

      String autoCacheFilePath = CachePath + File.separator
                               + autoShotNumber + "-" + autoPort + ".txt";
      File autoCacheFile = new File( autoCacheFilePath );
      autoReader = new BufferedReader( new FileReader( autoCacheFile ) );

      Vector autoCachedData = new Vector();

      // Ignore the headers
      String autoFileContents = autoReader.readLine();
      autoFileContents = autoReader.readLine();

      int tabIndex = 0;
      while( ( autoFileContents = autoReader.readLine() ) != null )
      {
        tabIndex = autoFileContents.indexOf( '\t' );
        autoCachedData.addElement( Double.valueOf( autoFileContents.substring( tabIndex + 1 ) ) );
      }
      autoReader.close();

      double[] autoData = new double[autoCachedData.size()];
      for( int i = 0; i < autoData.length; i++ )
        autoData[i] = ((Double)autoCachedData.elementAt(i)).doubleValue();

      return new PortData( autoShotNumber, autoPortInfo, autoData );
    }
    catch( InfomationException autoInfoExcpt )
    {
      JOptionPane.showMessageDialog( null, autoInfoExcpt.toString(), "Error", JOptionPane.ERROR_MESSAGE );
      return null;
    }
    catch( FileNotFoundException e )
    {
      return null;
    }
    catch( IOException e )
    {
      return null;
    }
    finally
    {
      try{
        autoReader.close();
      }
      catch( IOException e ){}
    }
  }

  // Cache file format ( 2002 Aug 23 )
  //  170111 - NLCC               ( shot number - port name )
  //  time   NLCC                 ( value name(time), tab, port value )
  //  50  200                     ( value, tab, port value )
  //  ...


  // Purpose   : Store data
  // Argument  : Stored PortData class
  // Return    : void
  public void storeToCache( PortData autoStoreData )
  {
    BufferedWriter autoWriter = null;
    try
    {
      PortInfomation autoPortInfo = autoStoreData.getPortInfo();
      final String autoPort = autoPortInfo.getPortName();

      double[] autoData = autoStoreData.getDataY();
      if( autoData == null )
        return;
      if( autoData.length <= 0 )
        return;

      File autoCacheFile = new File( CachePath, autoStoreData.getIdentity() + ".txt" );
      autoWriter = new BufferedWriter( new FileWriter( autoCacheFile ) );

      String autoWriteContents = "";

      double autoSampling = autoPortInfo.getSamplingTime() / 1000.0;
      double autoTrigger = autoPortInfo.getTriggerTime();
      double autoCurrentTime = autoTrigger;

      // Number format
      DecimalFormat autoTimeFormat = new DecimalFormat();
      DecimalFormat autoDataFormat = new DecimalFormat();
      autoTimeFormat.setMaximumFractionDigits( 5 );
      autoTimeFormat.setMinimumFractionDigits( 5 );
      autoDataFormat.setMaximumFractionDigits( 7 );
      autoDataFormat.setMinimumFractionDigits( 7 );
      autoDataFormat.setGroupingSize( 20 );

      // First write headers
      autoWriteContents = autoStoreData.getIdentity();
      autoWriter.write( autoWriteContents );
      autoWriter.newLine();
      autoWriteContents = "Time(ms)\t" + autoPort;
      autoWriter.write( autoWriteContents );
      autoWriter.newLine();

      for( int i = 0; i < autoData.length; i++ )
      {
        autoCurrentTime = autoTrigger + autoSampling * i;
        autoWriteContents = autoTimeFormat.format( autoCurrentTime )
                          + "\t"
                          + autoDataFormat.format( autoData[i] );

        autoWriter.write( autoWriteContents );
        autoWriter.newLine();
      }
      autoWriter.close();
    }
    catch( IOException e ){}
    finally
    {
      try{
        if( autoWriter != null )
          autoWriter.close();
      }
      catch( IOException e ){}
    }
  }


  // Purpose   : Cache directory file amount management
  // Argument  : void
  // Return    : void
  public void cacheManagement()
  {
    // If do not limit to cache
    if( MaxCacheFileAmount == -1 )
      return;

    // else
    String[] autoCachedFileNameList = new File( CachePath ).list();

    // If cache file amount is less than MaxCacheFileAmount
    if( autoCachedFileNameList.length <= MaxCacheFileAmount )
      return;

    else // Else if more than.
    {
      int i = 0;

      File autoCheckedFile  = null;
      Long autoLongValue    = null;
      Vector autoTimeStamp  = new Vector( autoCachedFileNameList.length );
      Vector autoCacheFile  = new Vector( autoCachedFileNameList.length );

      // Get file list and time stamp list
      for( i = 0; i < autoCachedFileNameList.length; i++ )
      {
        // Get a cache file object
        autoCheckedFile = new File( CachePath, autoCachedFileNameList[i] );
        // Get timestamp
        autoTimeStamp.addElement( String.valueOf( autoCheckedFile.lastModified() ) );
        // And add
        autoCacheFile.addElement( autoCheckedFile );
      }

      // Quick sorting for last modified time
      autoTimeStamp = ViewerDatas.quickSort( autoTimeStamp );

      // Determine the amount of file to have to delete
      // At here, about left half of max cache file amount
      // ex,) MaxCacheFileAmount = 200, autoCachedFileList.length = 234,
      //      then delete about 134 files (remain 100 files that is half of Max cache amount)
      int autoDeletedFileAmount = autoCachedFileNameList.length - MaxCacheFileAmount / 2;

      // Delete Index
      String autoIndex = autoTimeStamp.elementAt( autoDeletedFileAmount ).toString();

      // Delete caches
      File autoDeleteFile = null;
      String autoFileTimeString = null;
      for( i = 0; i < autoCachedFileNameList.length; i++ )
      {
        autoDeleteFile = (File)autoCacheFile.elementAt( i );
        autoFileTimeString = new Long( autoDeleteFile.lastModified() ).toString();
        // If this file is modified before index, then delete it.
        if( autoFileTimeString.compareTo( autoIndex ) < 0  )
          autoDeleteFile.delete();
      }
    }
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

        // Shot number
        if( autoKey.equals( "CachePath" ) )
          changedCacheDir( autoValue );

        else if( autoKey.equals( "MaxCache" ) )
          MaxCacheFileAmount = Integer.parseInt( autoValue );

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
      autoMessage[0] = "設定ファイル中でキャッシュ設定の読み込み時に不明なパラメータを見つけました。";
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
    autoWriter.write( "/*------ Caches       ------*/" );
    autoWriter.newLine();

    // Content
    String autoWriteContent = null;

    autoWriteContent = "CachePath\t\t\t" + CachePath;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriteContent = "MaxCache\t\t\t" + MaxCacheFileAmount;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriter.write( "/*------ End ------------*/" );
    autoWriter.newLine();
  }
}