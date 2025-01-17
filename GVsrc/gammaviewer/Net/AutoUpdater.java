/*
GAMMA Viewer - AutoUpdater : Thread implementation for auto update mode
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


GAMMA Viewer - AutoUpdater : オートアップデートのためのスレッドクラス
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

/*
    This AutoUpdater was modified by TAKAYUKI KOBAYASHI at July 6th, 2006.
    The data accumulation systems were changed in 2005. Then we have to 
    modify the data download procedure. First change was done in 2005.
    This is last modification in 2006.
    
    The original source code was applyed the GPL lisence and copyright 
    is owned by Eisuke Tsuda. This modified source code also applyed GPL
    lisence and copyright of modified part is owned by Takayuki Kobayashi.

    GAMMA Viewr - AutoUpdater (Modified)
    Copyright (C) 2005-2006 Takayuki Kobayashi
*/

package gammaviewer.Net;

import gammaviewer.InfomationException;
import gammaviewer.Data.*;

import javax.swing.JOptionPane;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.net.*;
import java.util.*;


public class AutoUpdater extends BasicConnectionMode
{
//-----------------------------------------
/*  Fields  */
//-----------------------------------------


// Current shot number
String CurrentNumber = null;


//-----------------------------------------
/*  Constructors  */
//-----------------------------------------

public AutoUpdater( NetworkManager autoNetMgr, ViewerMode autoAutoUpdate )
{
super( autoNetMgr, autoAutoUpdate );
}


//-----------------------------------------
/*  Methods  */
//-----------------------------------------



// Purpose   : Check there comes a new shot
// Argument  : void
// Return    : true if there is new shot, false if not
private boolean hasNewShot() throws InfomationException, InterruptedException
{
	// Get path to shot_no file
	String autoShot_NoPath = super.ConnectHost.getShotPath() + "/shot_no";
	
	// Get shot_no file.
    //------2005 tkobaya modified----------
	String autoShotNo = super.getPortDataAsString( autoShot_NoPath ).trim();
	
	if( autoShotNo.length() < 1 )
	  throw new InfomationException( "shot_noファイルを転送できませんでした。最新のショットがわからないためAutoUpdateを終了します" );
	
	// Get shot number string
	int autoPos = autoShotNo.lastIndexOf( "/" );
	String autoShotNoItself = autoShotNo.substring( autoPos + 1 );
	
	if( !autoShotNoItself.equals( CurrentNumber ) || CurrentNumber == null )
	{
	  CurrentNumber = autoShotNoItself;
	  return true;
	}
	else
	  return false;
}


// Purpose   : To start this thread
// Argument  : void
// Return    : void
public void run()
{
try
{
  // Shot number to have to download
  String autoShotNo = "";
  // sleep time (default 15 second)
  long autoSleepTime = 15 * 1000;
  // Whether this is first time or not
  boolean autoFirstTime = true;

  while( true )
  {
    super.login();

    NetMgr.showMessage( "新しいショットがあるかチェックしています" );

    if( this.hasNewShot() ) // There is a new shot
    {
      // Wait all datas are written in server
      Thread.sleep( 2000 );

      // Shot list
      Vector autoShotList = new Vector(1);
      autoShotList.addElement( this.CurrentNumber );

      // Get port data
      Vector autoData = super.getPortDatas( autoShotList, super.IdentMode.getPortList() );
      isStopSignal();

      // Set data
      super.NetMgr.getData().setShotData( autoData );
      isStopSignal();

      super.NetMgr.updateGraphics();

      //------2005 tkobaya modified----------
      // Wait all datas are written in server
      // Then wait 90 seconds.
      super.NetMgr.showMessage( "90秒後にこのショットを再確認します" );
      Thread.sleep( 90*1000 );

      // Shot list
      autoShotList = new Vector(1);
      autoShotList.addElement( this.CurrentNumber );

      // Get port data
      autoData = super.getPortDatas( autoShotList, super.IdentMode.getPortList() );
      isStopSignal();

      // Set data
      super.NetMgr.getData().setShotData( autoData );
      isStopSignal();

      super.NetMgr.updateGraphics();
      //--------------------------------------

      if( autoFirstTime )
        autoFirstTime = false;
      else
        // Change sleep time to 10 minites
      	//------2005 tkobaya modified----
        //autoSleepTime = 10 * 60 * 1000;
      	autoSleepTime = 9 * 60 * 1000;
    }
    else // If there is no new shot, then change sleep time to 15 second
      autoSleepTime = 15 * 1000;

    super.logout();

    super.NetMgr.showMessage( "次のショットを待っています" );

    // Sleep 10 minites or 15 seconds.
    Thread.sleep( autoSleepTime );
  }
}
catch( UnknownHostException autoHostExcpt )
{
  JOptionPane.showMessageDialog( null, "ホストが見つかりません。確認してみてください。", "Error", JOptionPane.ERROR_MESSAGE );
}
catch( InfomationException autoInfoExcpt )
{
  JOptionPane.showMessageDialog( null, autoInfoExcpt.toString(), "Error", JOptionPane.ERROR_MESSAGE );
}
catch( InterruptedException autoItrptExcpt )
{
  return;
}
finally
{
  super.logout();
  super.NetMgr.finishDownloading();
}
}
}
