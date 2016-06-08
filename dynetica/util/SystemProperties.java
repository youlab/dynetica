/***************************************************************************
                          SystemProperties.java  -  description
                             -------------------
    begin                : Mon Mar 18 2000
    copyright            : (C) 2000 by Lingchong You
    email                : you@cae.wisc.edu
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package dynetica.util;

public interface SystemProperties {
    public final static String NEWLINE = System.getProperty("line.separator");
    public final static String FILESEPARATOR = System
            .getProperty("file.separator");
}
