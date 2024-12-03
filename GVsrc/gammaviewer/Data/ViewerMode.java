/*
    GAMMA Viewer - ViewerMode : Interface of data class and controller dialog
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


    GAMMA Viewer - ViewerMode : データクラスとショット入力ダイアログ間のインターフェイス
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

import javax.swing.JOptionPane;
import java.util.Vector;
import java.io.*;


public class ViewerMode implements ViewerConstants
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  int Mode = MODE_CHOOSESHOT;

  // ショットナンバー、ポート名 リスト
  // Elements ; String
  Vector ShotList = new Vector();
  Vector PortList = new Vector();

  // Host name to connect to
  String HostToConnect = new String( "kasumi.prc.tsukuba.ac.jp" );

  // whether do caching or not
  boolean doCache = true;


  //-----------------------------------------
  /*  Constructors  */
  //-----------------------------------------

  public ViewerMode( int autoNewMode )
  {
    Mode = autoNewMode;
    if( autoNewMode == MODE_AUTOUPDATE )
      doCache = false;
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : Gets
  // Argument  : void
  // Return    : Return each parameters

  // Return identitied mode
  public int getMode()                { return Mode; }
  public String getHostToConnect()    { return HostToConnect; }
  // Return shot list ( as encoded string )
  public String getShotListAsString() { return EncodeString( ShotList ); }
  // Return port list ( as string )
  public String getPortListAsString() { return VectorToStringOfList( PortList ); }
  // Return vectorized shot and port list
  public Vector getShotList()         { return ShotList; }
  public Vector getPortList()         { return PortList; }
  public boolean doCache()            { return doCache; }


  // Purpose   : Sets
  // Argument  : Parameter to set
  // Return    : void
  public void setConnectionHost( String h )     { HostToConnect = h; }
  public void setShotList( String autoStringList ) throws InfomationException
  {
    ShotList = DecodeString( autoStringList );
  }
  public void setPortList( String autoStringList ) throws InfomationException
  {
    PortList = StringToVectorOfList( autoStringList );
  }

  public void doCache( boolean autodoCache )    { doCache = autodoCache; }



  // Purpose   : Change vector to camma separated string
  // Argument  : Vector
  // Return    : Camma separated string
  private String VectorToStringOfList( Vector autoListVector )
  {
    StringBuffer autoBuffer = new StringBuffer();

    // If there are no elements in ListVector
    if( autoListVector.size() < 1 )
      return "";

    autoBuffer.append( autoListVector.elementAt(0) );
    for( int i = 1; i < autoListVector.size(); i++ )
    {
      autoBuffer.append( "," );
      autoBuffer.append( (String)autoListVector.elementAt(i) );
    }
    return autoBuffer.toString();
  }


  // Purpose   : Encode to fomatted string
  //             If there are continuous values, then encoding
  //             ex 333,334,335.444  -> 333-5,444
  // Argument  : Vector
  // Return    : Camma or bar separated string
  private String EncodeString( Vector autoListVector )
  {
    StringBuffer autoBuffer = new StringBuffer();
    // Current value
    int autoPrivousValue = 0;
    String autoPrivousString = null;
    // Next value
    int autoCurrentValue = 0;
    String autoCurrentString = null;
    // Left and right operand
    // ( left is 180000 and right is 7, in such as string "180000-7" )
    String autoLeftOperand = null;
    String autoRightOperand = null;

    if( autoListVector.size() <= 0 )
      return "";

    boolean autoNeedbarSeparation = false;

    // First get 0 index value
    autoPrivousString = (String)autoListVector.elementAt( 0 );
    autoPrivousValue = Integer.parseInt( autoPrivousString );
    autoBuffer.append( autoPrivousString );

    for( int i = 1; i < autoListVector.size(); i++ )
    {
      autoCurrentString = (String)autoListVector.elementAt( i );
      autoCurrentValue = Integer.parseInt( autoCurrentString );

      // Check to need bar separation
      if( autoCurrentValue == autoPrivousValue + 1 )
      {
        // If not still detarmine left operand
        if( !autoNeedbarSeparation )
          autoLeftOperand = autoPrivousString;
        autoNeedbarSeparation = true;
      }
      else
      {
        if( autoNeedbarSeparation )
        {
          autoRightOperand = autoPrivousString;

          // Chenge privious string
          int j = 0;
          for( j = 0; autoLeftOperand.charAt(j) == autoRightOperand.charAt(j)
                     && j < autoLeftOperand.length();
                     j++ )
            ;
          autoRightOperand = autoRightOperand.substring( j );

          autoBuffer.append( "-" + autoRightOperand + ","+ autoCurrentString );
          autoNeedbarSeparation = false;
        }
        else
          autoBuffer.append( "," + autoCurrentString );
      }
      autoPrivousValue = autoCurrentValue;
      autoPrivousString = autoCurrentString;
    } // For

    if( autoNeedbarSeparation )
    {
      autoRightOperand = autoPrivousString;

      // Chenge privious string
      int j = 0;
      for( j = 0; autoLeftOperand.charAt(j) == autoRightOperand.charAt(j)
                && j < autoLeftOperand.length();
                j++ )
        ;
      autoRightOperand = autoRightOperand.substring( j );

      autoBuffer.append( "-" + autoRightOperand );
    }

    return autoBuffer.toString();
  }


  // Purpose   : Change camma separated string to vector
  // Argument  : Camma separated string
  // Return    : Vector
  private Vector StringToVectorOfList( String autoCammaSeparatedString ) throws InfomationException
  {
    Vector autoList = new Vector();

    if( autoCammaSeparatedString.length() < 1 )    // If less than 3 length string
      return autoList;

    int autoStart = 0;
    int autoEnd = 0;

    // Start to get
    autoEnd = autoCammaSeparatedString.indexOf( "," );
    while( autoEnd > -1 )
    {
      autoList.addElement( autoCammaSeparatedString.substring( autoStart , autoEnd ) );
      autoStart = autoEnd + 1;
      autoEnd = autoCammaSeparatedString.indexOf( ",", autoStart );
    }
    autoList.addElement( autoCammaSeparatedString.substring( autoStart ) );

    return autoList;
  }


  // Purpose   : Return a decoded string from formatted string as shot and port expression
  //             like 333-335,444 -> 333,334,335,444
  // Argument  : Camma an bar separated string
  // Return    : Vector list
  private Vector DecodeString( String autoString ) throws InfomationException
  {
    // Vectorlized list
    Vector autoList = new Vector();

    // if there are no shot numbers.
    if( autoString.length() <= 0 )
      return autoList;

    // Position of camma
    int autoLeftCammaPos = 0;
    int autoRightCammaPos = 0;
    // Position of bar
    int autoBarPos = -1;
    // Left and right oprerand
    String autoLeftOperand = null;
    String autoRightOperand = null;
    int autoIntLeftOperand = 0;
    int autoIntRightOperand = 0;

    // Going to be replaced string buffer
    String autoStringCopy = new String( autoString );
    StringBuffer autoDecodedString = new StringBuffer( autoStringCopy );
    try
    {
      autoBarPos = autoStringCopy.indexOf( "-", autoBarPos + 1 );
      while( autoBarPos > 0 )
      {
        // Get left operand
        autoLeftCammaPos = autoStringCopy.lastIndexOf( ",", autoBarPos - 1 );
        autoLeftOperand = autoStringCopy.substring( autoLeftCammaPos + 1, autoBarPos );

        // Get right operand
        autoRightCammaPos = autoStringCopy.indexOf( ",", autoBarPos + 1 );
        if( autoRightCammaPos == -1 )
          autoRightCammaPos = autoStringCopy.length();
        autoRightOperand = autoStringCopy.substring( autoBarPos + 1, autoRightCammaPos );

        // Get left and right operand as integer
        autoIntLeftOperand = Integer.parseInt( autoLeftOperand );
        autoIntRightOperand = Integer.parseInt( autoRightOperand );

        // This case, such as "180100-110", then change like "110 -> 180110"
        if( autoIntLeftOperand > autoIntRightOperand )
        {
          int autoIndexOfLeftOperand = autoLeftOperand.length();
          int autoIndexOfRightOperand = autoRightOperand.length();
          String autoPrefix = autoLeftOperand.substring( 0, autoIndexOfLeftOperand - autoIndexOfRightOperand );

          autoRightOperand = autoPrefix + autoRightOperand;
          autoIntRightOperand = Integer.parseInt( autoRightOperand );
        }

        int autoIncrement = autoIntRightOperand - autoIntLeftOperand;

        // If user want to show a lots
        if( autoIncrement >= 30 )
          throw new InfomationException("30ショット以上を表示することは申し訳ないですが推奨できません");
        // If something trroble happen, autoIncrement gets to be negative
        if( autoIncrement < 0 )
          throw new InfomationException("表示するショット数が負になりました。入力値を見直してみてください");

        StringBuffer autoDecoding = new StringBuffer( String.valueOf( autoIntLeftOperand ) );
        for( int i = 1; i <= autoIncrement; i++ )
          autoDecoding.append( "," + String.valueOf( autoIntLeftOperand + i ) );

        // In java 1.1 there is no method StringBuffer.replace,
        // So implement same functional code
        // What want to do is this.
        // autoDecodedString.replace( autoLeftCammaPos + 1, autoRightCammaPos, autoDecoding.toString() );
        //
        StringBuffer autoTemp = new StringBuffer();
        autoTemp.append( autoDecodedString.toString().substring( 0, autoLeftCammaPos + 1 ) );
        autoTemp.append( autoDecoding.toString() );
        autoTemp.append( autoDecodedString.toString().substring( autoRightCammaPos ) );

        autoStringCopy = autoTemp.toString();

//        autoDecodedString.replace( autoLeftCammaPos + 1, autoRightCammaPos, autoDecoding.toString() );
//        autoStringCopy = autoDecodedString.toString();

        autoBarPos = autoStringCopy.indexOf( "-", autoBarPos + 1 );
      }
    }
    catch( NumberFormatException e )
    {
      throw new InfomationException("文字が数字になっていないようです");
    }

    // From here, the copy of method this.StringToVectorOfList()
    int autoStart = 0;
    int autoEnd = 0;

    // Start to get
    autoEnd = autoStringCopy.indexOf( "," );
    while( autoEnd > -1 )
    {
      autoList.addElement( autoStringCopy.substring( autoStart , autoEnd ) );
      autoStart = autoEnd + 1;
      autoEnd = autoStringCopy.indexOf( ",", autoStart );
    }
    autoList.addElement( autoStringCopy.substring( autoStart ) );

    return autoList;
  }


  // Purpose   : Copy contents of each mode, for unwanted contents change when downloading
  // Argument  : Source of copy
  // Return    : void
  public void copyContents( ViewerMode autoSource )
  {
    ShotList = (Vector)autoSource.getShotList().clone();
    PortList = (Vector)autoSource.getPortList().clone();
    HostToConnect = new String( autoSource.getHostToConnect() );
    doCache = autoSource.doCache();
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
        // Get kay and value
        autoKey = autoContent.substring( 0, autoContent.indexOf( "\t" ) );
        autoValue = autoContent.substring( autoContent.lastIndexOf( "\t" ) + 1 );

        // Shot number
        if( autoKey.equals( "ShotNo" ) )
          ShotList.addElement( autoValue );

        else if( autoKey.equals( "Port" ) )
          PortList.addElement( autoValue );

        else if( autoKey.equals( "ConnectHost" ) )
          HostToConnect = autoValue;

        else if( autoKey.equals( "DoCache" ) )
          doCache = Boolean.valueOf( autoValue ).booleanValue();

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
      autoMessage[0] = "設定ファイル中でモードの読み込み時に不明なパラメータを見つけました。";
      autoMessage[1] = "以下の値は無視されます。";
      for( int i = 2; i < autoMessage.length; i++ )
        autoMessage[i] = autoUnknowKeys.elementAt(i-2).toString();

      JOptionPane.showMessageDialog( null, autoMessage, "Infomation", JOptionPane.INFORMATION_MESSAGE );
    }
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
  public void serialize( BufferedWriter autoWriter ) throws IOException
  {
    // Write header
    if( Mode == MODE_AUTOUPDATE )
      autoWriter.write( "/*------ AutoUpdater  ------*/" );
    else
      autoWriter.write( "/*------ ShotChooser  ------*/" );

    autoWriter.newLine();

    int i = 0;
    String autoWriteContent = null;

    // Save shot number
    for( i = 0; i < ShotList.size(); i++ )
    {
      autoWriteContent = "ShotNo\t\t\t" + ShotList.elementAt(i).toString();
      autoWriter.write( autoWriteContent );
      autoWriter.newLine();
    }

    // Save port
    for( i = 0; i < PortList.size(); i++ )
    {
      autoWriteContent = "Port\t\t\t" + PortList.elementAt(i).toString();
      autoWriter.write( autoWriteContent );
      autoWriter.newLine();
    }

    // Save connect host
    autoWriteContent = "ConnectHost\t\t\t" + HostToConnect;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // Save whether caching or not
    autoWriteContent = "DoCache\t\t\t" + doCache;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriter.write( "/*------ End ------------*/" );
    autoWriter.newLine();
  }
}