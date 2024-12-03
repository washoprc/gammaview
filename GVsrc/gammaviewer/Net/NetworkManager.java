/*
    GAMMA Viewer - NetworkManager : Network management class
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


    GAMMA Viewer - NetworkManager : ネットワーク制御クラス
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
import gammaviewer.ViewerConstants;
import gammaviewer.Data.CacheManager;
import gammaviewer.Data.ViewerDatas;
import gammaviewer.Data.ViewerMode;
import gammaviewer.Data.Host;
import gammaviewer.Graphics.GraphicsManager;
import gammaviewer.UI.WindowManager;
import gammaviewer.UI.LoginDialog;

import java.util.Vector;


public class NetworkManager implements ViewerConstants
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  // Reference to data class
  ViewerDatas Datas = null;

  // Managers
  WindowManager   WndMgr = null;
  GraphicsManager GrpMgr = null;


  // Network connectors.
  Thread              DownloadThread  = null;
  BasicConnectionMode DownloadMode    = null;


  //-----------------------------------------
  /*  Constructors  */
  //-----------------------------------------

  public NetworkManager( ViewerDatas autoData )
  {
    Datas = autoData;
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : Initialization
  // Argument  : Window and graphics managers
  // Return    : void
  public void init( WindowManager autoWngMgr, GraphicsManager autoGrpMgr )
  {
    WndMgr = autoWngMgr;
    GrpMgr = autoGrpMgr;
  }


  // Purpose   : finalization
  // Argument  : void
  // Return    : void
  public void finalize()
  {
    // If thread is alive, kill it forcly
    if( this.isNetThreadAlive() )
      this.stopDownloading();
  }


  // Purpose   : get data contener
  // Argument  : void
  // Return    : The only data class of this application
  public ViewerDatas getData()  { return Datas; }


  // Purpose   : Access to cache manager
  // Argument  : void
  // Return    : cache manager
  public CacheManager getCacheManager()  { return Datas.getCacheManager(); }


  // Purpose   : To get login dialog object when login faled
  // Argument  : Host object for infomation shown in the dialog
  // Return    : LoginDialog object
  public LoginDialog getLoginDialog( Host autoHost )
  {
    return WndMgr.getLoginDialog( autoHost );
  }


  // Purpose   : Start download or search cache thread
  // Argument  : void
  // Return    : void
  public void start( ViewerMode autoMode )
  {
    // If thread is alive, kill it forcly
    if( this.isNetThreadAlive() )
      this.stopDownloading();

    // Create download mode object
    if( autoMode.getMode() == MODE_AUTOUPDATE )
      DownloadMode = new AutoUpdater( this, autoMode );
    else
      DownloadMode = new GetShot    ( this, autoMode );

    // Create thread
    DownloadThread = new Thread( DownloadMode, "Gammaviewer DL thread" );

    // And start
    DownloadThread.start();
  }


  // Purpose   : To know download thread is alive or not
  // Argument  : void
  // Return    : True if alive either two thread, false if not
  public boolean isNetThreadAlive()
  {
    // If thread not equal null, then returns DownloadThread.isAlive(),
    // Else return null;
    return DownloadThread != null ? DownloadThread.isAlive() : false;
  }


  // Purpose   : Stop downloading
  // Argument  : void
  // Return    : void
  public void stopDownloading()
  {
    if( DownloadThread != null && DownloadThread.isAlive() )
    {
      // First stop thread
      DownloadMode.stopThread();
      this.DownloadThread.interrupt();

      boolean autoIsThreadAlive = DownloadThread.isAlive();
      int autoCounter = 0;
      try
      {
        while( autoIsThreadAlive || autoCounter > 30 )
        {
          this.DownloadThread.interrupt();
          Thread.sleep( 200 );
          autoIsThreadAlive = DownloadThread.isAlive();
          autoCounter++;
        }
      }
      catch( InterruptedException autoItrptExcpt ) {}

      // Interrupting.
      if( autoCounter > 30 )
      {
        DownloadThread.interrupt();
        DownloadMode.logout();
      }
    }
  }


  // Purpose   : Show some messages needed to tell when downloading
  // Argument  : String to show
  // Return    : void
  public void showMessage( String autoMsg )     { WndMgr.showMessage( autoMsg ); }


  // Purpose   : Tell that download is finished to Graphics Manager
  // Argument  : void
  // Return    : void
  public void updateGraphics()
  {
    GrpMgr.draw();
  }


  // Purpose   : Tell window manager that download-thread is finished
  // Argument  : void
  // Return    : void
  public void finishDownloading()
  {
    WndMgr.finishDownloading();
  }
}