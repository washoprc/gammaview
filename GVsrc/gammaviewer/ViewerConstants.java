/*
    GAMMA Viewer - ViewerConstants : Interface of constants defined for this application
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


    GAMMA Viewer - ViewerConstants : ���ץꥱ�������Τ����������饹
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


public interface ViewerConstants
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  /* Mode constants */
  static final int MODE_CHOOSESHOT = 1;
  static final int MODE_AUTOUPDATE = 2;

  /* Status of downloading */
  static final int STATUS_BEGIN = 3;
  static final int STATUS_END   = 4;

  /* Server Type */
  static final int TYPE_FTP   = 5;
  static final int TYPE_SMB   = 6;
  static final int TYPE_MAC   = 7;
  static final int TYPE_LOCAL = 8;

  /* Serialization mode */
  static final int SERIALIZE_MODE_READ  = 10;
  static final int SERIALIZE_MODE_WRITE = 11;

  static final String BUILD            = "20030310-Final";
  static final String APP_NAME         = "Gamma Viewer";
  static final String APP_SHORT_NAME   = "gv";

  /* System Environment */
  static final String JREVERSION  = System.getProperty( "java.version" );
  static final String HOME        = System.getProperty( "user.home" );
  static final String CURRENTDIR  = System.getProperty( "user.dir" );

  /* System file separator */
  static final String UNIXFILESEPARATOR  = "/";
  static final String WINFILESEPARATOR   = "\\";
}
