/*
    GAMMA Viewer - GAMMAViewer : Main application class
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


    GAMMA Viewer - GAMMAViewer : ���ץꥱ������󥯥饹
    Copyright (C) 2002-2003  ���ıѲ�


    ���Υץ����ϥե꡼���եȥ������Ǥ������ʤ��Ϥ���򡢥ե꡼����
    �ȥ��������Ĥˤ�ä�ȯ�Ԥ��줿 GNU ���̸������ѵ��������(�С�����
    ��2������˾�ˤ�äƤϤ���ʹߤΥС������Τ����ɤ줫)��������
    �β��Ǻ����ۤޤ��ϲ��Ѥ��뤳�Ȥ��Ǥ��ޤ���

    ���Υץ�����ͭ�ѤǤ��뤳�Ȥ��ä����ۤ���ޤ�����*������̵��
    ��* �Ǥ������Ȳ�ǽ�����ݾڤ��������Ū�ؤ�Ŭ�����ϡ������˼����줿
    ��Τ�ޤ�����¸�ߤ��ޤ��󡣾ܤ�����GNU ���̸������ѵ���������
    ������������

    ���ʤ��Ϥ��Υץ����ȶ��ˡ�GNU ���̸������ѵ���������ʣ��ʪ��
    ����������ä��Ϥ��Ǥ����⤷������äƤ��ʤ���С��ե꡼���եȥ���
    �����Ĥޤ����ᤷ�Ƥ�������(����� the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA)��
*/

package gammaviewer;

import gammaviewer.Data.ViewerDatas;
import gammaviewer.UI.WindowManager;
import gammaviewer.Graphics.GraphicsManager;
import gammaviewer.Net.NetworkManager;

import javax.swing.UIManager;


public class GAMMAViewer
{

  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // A data class instance
  ViewerDatas Datas = new ViewerDatas();

  // Managers
  GraphicsManager GrpMgr = null;
  NetworkManager  NetMgr = null;
  WindowManager   WndMgr = null;


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------


  /* Construction of application */
  public GAMMAViewer() {

/*
    // Show title image.
    Graphics g = new Graphics();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
*/

    // Create managers
    GrpMgr = new GraphicsManager( Datas );
    NetMgr = new NetworkManager( Datas );
    WndMgr = new WindowManager( Datas );

    // Init managers
    GrpMgr.init( WndMgr, NetMgr );
    NetMgr.init( WndMgr, GrpMgr );
    WndMgr.init( NetMgr, GrpMgr );
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------

  /* Main method */
  public static void main(String[] args)
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    new GAMMAViewer();
  }
}