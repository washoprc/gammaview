/*
    GAMMA Viewer - PortInfomation : Miscurious port infomation
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


    GAMMA Viewer - PortInfomation : ポート情報クラス
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

import gammaviewer.Data.Filter.*;
import gammaviewer.*;

import javax.swing.JOptionPane;
import java.io.*;
import java.util.*;


public class PortInfomation
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  // Name
  String Name = null;
  // Name of related ports
  Vector RelatedPorts = new Vector();
  // Sampling time
  double SamplingTime = 25;
  // Trgger time
  double TriggerTime = 50;

  // data max and min values.
  double MaxValue = 5.0;
  double MinValue = -5.0;
  double MaxDrawValue = 5.0;
  double MinDrawValue = -5.0;

  // Start and End Time
  double StartTime = 50;
  double EndTime = 250;

  // Filter
  AbstractFilter Filter = null;

  //-----------------------------------------
  /*  Constructors  */
  //-----------------------------------------

  public PortInfomation()
  {
    Name = "EE0100";
    RelatedPorts.addElement( Name );
    Filter = new NormalFilter10();
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------

  // gets and sets
  public String getPortName()         { return Name; }
  public String getRelatedPorts()     { return this.convertToString( RelatedPorts ); }
  public double getSamplingTime()     { return SamplingTime; }
  public double getTriggerTime()      { return TriggerTime; }
  public double getMaxValue()         { return MaxValue; }
  public double getMinValue()         { return MinValue; }
  public double getMaxDrawValue()     { return MaxDrawValue; }
  public double getMinDrawValue()     { return MinDrawValue; }
  public double getStartTime()        { return StartTime; }
  public double getEndTime()        { return EndTime; }
  public String getFilterName()       { return Filter.getFilterName(); }
  public AbstractFilter getFilter()   { return Filter; }
  public Vector getDownloadFileList() { return RelatedPorts; }

  public void setPortName( String autoPortName ) throws InfomationException
  {
    if( autoPortName.length() < 1 )
      throw new InfomationException( "ポート名は空欄にしないでください。" );
    else
      Name = autoPortName;
  }
  public void setRelatedPorts( String autoRelatedPorts ) throws InfomationException
  {
    if( autoRelatedPorts.length() < 1 )
      throw new InfomationException( "関連ポートは空欄にしないでください。" );
    else
      RelatedPorts = this.vectorizeString( autoRelatedPorts );
  }
  public void setSamplingTime( double autoNewSampling )  throws InfomationException
  {
    if( autoNewSampling < 0 )
      throw new InfomationException( "サンプリングに負の値が設定されています。" );
    else
      SamplingTime = autoNewSampling;
  }
  public void setTriggerTime ( double autoNewTrigger )  throws InfomationException
  {
    if( autoNewTrigger < 0 )
      throw new InfomationException( "トリガーに負の値が設定されています。" );
    else
      TriggerTime = autoNewTrigger;
  }
  public void setRange( double autoNewMax, double autoNewMin ) throws InfomationException
  {
    if( autoNewMax <= autoNewMin )
      throw new InfomationException( "最大値が最小値より小さくなっています" );
    else
    {
      MaxValue = autoNewMax;
      MinValue = autoNewMin;
    }
  }
  public void setDrawRange( double autoNewDrawMax, double autoNewDrawMin )
  {
    MaxDrawValue = autoNewDrawMax;
    MinDrawValue = autoNewDrawMin;
  }
  public void setTimeRange( double autoStart, double autoEnd ) throws InfomationException
  {
    if( autoStart >= autoEnd )
      throw new InfomationException( "プロット開始時間が終了時間より後になっています" );
    else
    {
      StartTime = autoStart;
      EndTime = autoEnd;
    }
  }
  public void setFilter( AbstractFilter autoNewFilter )   { Filter = autoNewFilter; }


  // Purpose   : Change vector to camma separated string
  // Argument  : Vector
  // Return    : Camma separated string
  private String convertToString( Vector autoListVector )
  {
    StringBuffer autoBuffer = new StringBuffer();

    // If there are no elements in ListVector
    if( autoListVector.size() < 1 )
      return new String();

    autoBuffer.append( autoListVector.elementAt(0) );
    for( int i = 1; i < autoListVector.size(); i++ )
    {
      autoBuffer.append( "," );
      autoBuffer.append( (String)autoListVector.elementAt(i) );
    }
    return autoBuffer.toString();
  }


  // Purpose   : Change camma separated string to vector
  // Argument  : Camma separated string
  // Return    : Vector
  private Vector vectorizeString( String autoCammaSeparatedString )
  {
    Vector autoPortList = new Vector();

    if( autoCammaSeparatedString.length() <= 0 )    // If 0 length string
      return autoPortList;

    int autoStart = 0;
    int autoEnd = 0;

    // Start to get
    autoEnd = autoCammaSeparatedString.indexOf( "," );
    while( autoEnd > -1 )
    {
      autoPortList.addElement( autoCammaSeparatedString.substring( autoStart , autoEnd ) );
      autoStart = autoEnd + 1;
      autoEnd = autoCammaSeparatedString.indexOf( ",", autoStart );
    }
    autoPortList.addElement( autoCammaSeparatedString.substring( autoStart ) );

    return autoPortList;
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
  public void unserialize( Vector autoContents, FilterLoader autoLdr ) throws IOException
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
        if( autoKey.equals( "PortName" ) )
        {
          try
          {
            if( autoValue.startsWith( "NLCC(" ) )
            {
              this.setUpNLCC( autoLdr.getFilter( "NL Filter" ) );
              return;
            }
            else if( autoValue.startsWith( "NLEB(" ) )
            {
              this.setUpNLEB( autoLdr.getFilter( "NL Filter" ) );
              return;
            }
            else
                Name = autoValue;
          }
          catch( InfomationException autoInfoExcpt ) {}
        }

        else if( autoKey.equals( "RelatedPorts" ) )
          RelatedPorts = vectorizeString( autoValue );

        else if( autoKey.equals( "Sampling" ) )
          SamplingTime = Double.valueOf( autoValue ).doubleValue();

        else if( autoKey.equals( "Trigger" ) )
          TriggerTime = Double.valueOf( autoValue ).doubleValue();

        else if( autoKey.equals( "MaxValue" ) )
          MaxValue = Double.valueOf( autoValue ).doubleValue();

        else if( autoKey.equals( "MinValue" ) )
          MinValue = Double.valueOf( autoValue ).doubleValue();

        else if( autoKey.equals( "MaxDrawValue" ) )
          MaxDrawValue = Double.valueOf( autoValue ).doubleValue();

        else if( autoKey.equals( "MinDrawValue" ) )
          MinDrawValue = Double.valueOf( autoValue ).doubleValue();

        else if( autoKey.equals( "StartTime" ) )
          StartTime = Double.valueOf( autoValue ).doubleValue();

        else if( autoKey.equals( "EndTime" ) )
          EndTime = Double.valueOf( autoValue ).doubleValue();

        else if( autoKey.equals( "Filter" ) )
        {
          try
          {
            Filter = autoLdr.getFilter( autoValue );
          }
          catch( InfomationException autoInfoExcpt )
          {
          }
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
      autoMessage[0] = "設定ファイル中でポート設定の読み込み時に不明なパラメータを見つけました。";
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
    autoWriter.write( "/*------ Port ------*/" );
    autoWriter.newLine();

    // Content
    String autoWriteContent = null;

    // Save Name
    autoWriteContent = "PortName\t\t\t" + Name;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // Name of related ports
    autoWriteContent = "RelatedPorts\t\t\t" + this.convertToString( RelatedPorts );
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // Sampling time
    autoWriteContent = "Sampling\t\t\t" + SamplingTime;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // Trgger time
    autoWriteContent = "Trigger\t\t\t" + TriggerTime;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // data max and min values.
    autoWriteContent = "MaxValue\t\t\t" + MaxValue;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();
    autoWriteContent = "MinValue\t\t\t" + MinValue;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // data max and min values.
    autoWriteContent = "MaxDrawValue\t\t\t" + MaxDrawValue;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();
    autoWriteContent = "MinDrawValue\t\t\t" + MinDrawValue;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // Plot start time

    autoWriteContent = "StartTime\t\t\t" + StartTime;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();
    autoWriteContent = "EndTime\t\t\t" + EndTime;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // Filter
    autoWriteContent = "Filter\t\t\t" + Filter.getFilterName();
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    // Write end section
    autoWriter.write( "/*------ End ------------*/" );
    autoWriter.newLine();
  }


  private void setUpNLCC( AbstractFilter autoNLFilter )
  {
    Name          = "NLCC";
    RelatedPorts  = this.vectorizeString( "MIAA01,MJC100,MJC200" );
    SamplingTime  = 25;
    TriggerTime   = 50;
    MaxValue      = 5;
    MinValue      = -5;
    MaxDrawValue  = 10;
    MinDrawValue  = 0;
    StartTime     = 50;
    EndTime       = 250;
    Filter = autoNLFilter;
  }
  private void setUpNLEB( AbstractFilter autoNLFilter )
  {
    Name          = "NLEB";
    RelatedPorts  = this.vectorizeString( "MIAA00" );
    SamplingTime  = 25;
    TriggerTime   = 50;
    MaxValue      = 5;
    MinValue      = -5;
    MaxDrawValue  = 10;
    MinDrawValue  = 0;
    StartTime     = 50;
    EndTime       = 250;
    Filter = autoNLFilter;
  }
}