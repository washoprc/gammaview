/*
    GAMMA Viewer - PortData : Class of containing port datas
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


    GAMMA Viewer - PortData : ポートデータ格納用クラス
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

import java.util.Hashtable;
import java.util.Vector;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class PortData
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  // Identified shot number
  String ShotNumber = null;

  // Identified infomation for this port
  PortInfomation PortInfo = null;

  // Data size (amount of data points)
  int DataSize = 2;
  // 4 means 4 byte for one data point
  // 2 means 2 byte

  // Data itself ( elements : double array (double[]) )
  double[] PortDataX = null;
  double[] PortDataY = null;

  // If there is something error happen, Error infomation is store to this string
  String ErrorInfo = "Data is still not Set";


  //-----------------------------------------
  /*  Constructors  */
  //-----------------------------------------

  public PortData()           {}
  public PortData( String autoNumber, PortInfomation autoPortInfo )
  {
    ShotNumber = autoNumber;
    PortInfo = autoPortInfo;
  }
  public PortData( String autoNumber, PortInfomation autoPortInfo, double[] autoDataY )
  {
    ShotNumber = autoNumber;
    PortInfo = autoPortInfo;
    PortDataY = autoDataY;
  }
  public PortData( String autoNumber, PortInfomation autoPortInfo, double[] autoDataX, double[] autoDataY )
  {
    ShotNumber = autoNumber;
    PortInfo = autoPortInfo;
    PortDataX = autoDataX;
    PortDataY = autoDataY;
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------

  // Purpose   : Get port data
  // Argument  : void
  // Return    : Array of double
  public double[] getDataX()   { return PortDataX; }
  public double[] getDataY()   { return PortDataY; }


  // Purpose   : Get port infomation
  // Argument  : void
  // Return    : Port infomation of this
  public PortInfomation getPortInfo() { return PortInfo; }


  // Purpose   : Get title identified this data
  // Argument  : void
  // Return    : String include shot number, port name and data unit
  public String getIdentity()
  {
    return ShotNumber + "-" + PortInfo.getPortName();
  }
  public String getTitle()
  {
    return ShotNumber + "-" + PortInfo.getPortName() + " (" + PortInfo.getFilter().getUnit() + ")";
  }

  // Purpose   : Get shot number
  // Argument  : void
  // Return    : Shot number of this data
  public String getErrorInfo() { return ErrorInfo; }


  // Purpose   : set port data from downloaded raw data
  // Argument  : downloaded raw datas
  // Return    : void
  public void setData( Hashtable autoRawDataTable )
  {
    // Keys used in this port
    Vector autoKeys = PortInfo.getDownloadFileList();
    // Raw data itself
    byte[] autoContent = null;
    // Size of data exclude headers
    int autoDataSize = 0;

    // Formatted raw data table
    Vector autoDatas = new Vector( autoKeys.size() );

    // Key
    String autoFileKey = null;

    int i = 0;
    for( i = 0; i < autoKeys.size(); i++ )
    {
      autoFileKey = this.ShotNumber + "/" + autoKeys.elementAt(i).toString();
      // Get raw data
      autoContent = (byte[])autoRawDataTable.get( autoFileKey );

      // Check whether error occer or not
      if( autoContent.length <= 0 )
      {
        ErrorInfo = "File Not Found";
      }
      else if( autoContent.length <= 160 )
        ErrorInfo = "Data is crushed";

      else // Correct data
      {
        // Pick up datas
        // valuable data size is    (autoContent.size() - 160) / DataSize
        //                                        ( 160 is header size )
        int[] autoIntegerData = new int[ ( autoContent.length - 160 ) / DataSize ];
        int autoIndex = 0, j = 0;
        for( autoIndex = 160; autoIndex < autoContent.length; autoIndex += DataSize )
          autoIntegerData[j++] = this.getInteger( autoContent, autoIndex, autoIndex + DataSize );

        // Add data list
        autoDatas.addElement( autoIntegerData );
      }
    }

    // Apply port oliented filter
    double[] autoAllPortData = PortInfo.getFilter().applyFilter( PortInfo, autoDatas );
    if( autoAllPortData == null )
      return;

    double autoStart = PortInfo.getStartTime();
    double autoEnd = PortInfo.getEndTime();
    double autoSampling = PortInfo.getSamplingTime() / 1000;
    double autoTrigger = PortInfo.getTriggerTime();

    int autoStartIndex = 0;
    int autoEndIndex = autoAllPortData.length;

    if( autoTrigger < autoStart )
      autoStartIndex = (int)( ( autoStart - autoTrigger ) / autoSampling );
    if( autoAllPortData.length * autoSampling + autoTrigger > autoEnd )
      autoEndIndex = (int)( ( autoEnd - autoTrigger ) / autoSampling );

    PortDataY = new double[ autoEndIndex - autoStartIndex ];
    int j = 0;
    for( i = autoStartIndex; i < autoEndIndex; i++ )
      PortDataY[j++] = autoAllPortData[i];
  }


  // Purpose   : Get some bytes-integer ( 2 or 4 or more ) from byte arraied vector
  //             Range is from start to end ( not include end )
  // Argument  : Vector sequence autoV, index of start and end.
  // Return    : integer
  private int getInteger( Vector autoArray, int autoStart, int autoEnd )
  {
    int autoValue = 0;
    for( int i = autoStart; i < autoEnd; i++ )
    {
      autoValue <<= 8;
      autoValue |= ((Integer)autoArray.elementAt(i)).intValue();
    }
    return autoValue;
  }
  private int getInteger( byte[] autoArray, int autoStart, int autoEnd )
  {
    int autoValue = 0;
    for( int i = autoStart; i < autoEnd; i++ )
    {
      autoValue <<= 8;
      autoValue += autoArray[i] & 0xff;
    }
    return autoValue;
  }
}