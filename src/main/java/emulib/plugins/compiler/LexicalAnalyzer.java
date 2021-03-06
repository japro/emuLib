/*
 * KISS, YAGNI, DRY
 *
 * (c) Copyright 2006-2017, Peter Jakubčo
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package emulib.plugins.compiler;

import java.io.IOException;
import java.io.Reader;


/**
 * Interface that implements lexical analyzer
 */
public interface LexicalAnalyzer {

    /**
     * Performs reset of the analyzer.
     *
     * Internal counters (actual position, actual column, row, etc.) should be cleared. Lexical analyzer should prepare
     * itself to start parsing from the beginning of the document.
     */
    void reset ();

    /**
     * Performs reset of the analyzer.
     *
     * Internal counters (actual position, actual column, row, etc.) should be cleared. Lexical analyzer should prepare
     * itself to start parsing from the beginning of the document.
     *
     * @param sourceCode <code>Reader</code> of the source code.
     * @param startLine from this line should lexical analyzer start, usually 0
     * @param startChar from this char should lexical analyzer start, usually 0
     * @param startColumn from this column should lexical analyzer start, usually 0
     */
    void reset (Reader sourceCode, int startLine, int startChar, int startColumn);

    /**
     * Gets next lexical symbol from source code, from actual position. This is
     * real implementation of lexical analyzer. After this call it should
     * increase internal counters to next unread text (actual position, actual
     * row, column, etc.)
     *
     * @throws java.io.IOException is thrown when the token could not be read
     * @return next found token
     */
    Token getSymbol () throws IOException;

}

