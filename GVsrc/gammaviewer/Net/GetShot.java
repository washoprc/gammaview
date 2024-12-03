/*
    GAMMA Viewer - GetShot : Class for shot choosen mode to get shots and ports datas
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


    GAMMA Viewer - GetShot : ����å�����⡼���ѤΥ��饹
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


package gammaviewer.Net;

import gammaviewer.InfomationException;
import gammaviewer.Data.*;

import javax.swing.JOptionPane;
import java.net.*;
import java.util.*;


public class GetShot extends BasicConnectionMode
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------



  //-----------------------------------------
  /*  Constructers  */
  //-----------------------------------------

  public GetShot( NetworkManager autoNetMgr, ViewerMode autoChooseShot )
  {
    super( autoNetMgr, autoChooseShot );
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : To start this thread
  // Argument  : void
  // Return    : void
  public void run()
  {
    try
    {
      super.NetMgr.getData().setShotData( super.getPortDatas( super.IdentMode.getShotList(),
                                                              super.IdentMode.getPortList() ) );
      super.NetMgr.updateGraphics();
    }
    catch( UnknownHostException autoHostExcpt )
    {
      JOptionPane.showMessageDialog( null, "�ۥ��Ȥ����Ĥ���ޤ��󡣳�ǧ���ƤߤƤ���������", "Error", JOptionPane.ERROR_MESSAGE );
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
