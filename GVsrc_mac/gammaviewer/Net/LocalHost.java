/*
    GAMMA Viewer - LocalHost : Class to read file on local
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


    GAMMA Viewer - LocalHost : ローカルのファイルを読むクラス
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

import java.net.InetAddress;
import java.io.*;


public class LocalHost implements ServerConnection
{
  public LocalHost()    {}

  public void CreateConnection( InetAddress addr, int port ) throws InfomationException {}
  public boolean login(String UserName, String Password) throws InfomationException  { return true; }
  public void logout()  {}


  // Purpose   : Get local file
  // Argument  : Local file path
  // Return    : ByteArray stream
  public ByteArrayOutputStream get( String autoFileName )
                              throws InfomationException,
                                     FileNotFoundException
  {
    try
    {
      ByteArrayOutputStream autoOutputStream = new ByteArrayOutputStream( 9000 );
      BufferedInputStream autoInput = new BufferedInputStream( new FileInputStream( autoFileName ) );

      int i = 0;
      while( ( i = autoInput.read() ) != -1 )
        autoOutputStream.write( i );

      autoInput.close();
      autoOutputStream.close();

      return autoOutputStream;
    }
    catch( FileNotFoundException autoFNFExcpt )
    {
      throw autoFNFExcpt;
    }
    catch( IOException autoIOExcpt )
    {
      throw new InfomationException( "ローカルファイルの読み込み中にエラーが発生しました。ファイルを読めません" );
    }
  }
}