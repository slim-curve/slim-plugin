//
// Cursor.java
//

/*
ImageJ software for multidimensional image processing and analysis.

Copyright (c) 2011, ImageJDev.org.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the names of the ImageJDev.org developers nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package imagej.slim.fitting.cursor;

/**
 * Container for cursor information.
 * 
 * @author Aivar Grislis
 */
public class Cursor implements ICursor {
    private int _promptStart;
    private int _promptStop;
    private int _decayStart;
    private int _decayStop;

    @Override
    public void setPromptStart(int bin) {
        _promptStart = bin;
    }

    @Override
    public int getPromptStart() {
        return _promptStart;
    }
    
    @Override
    public void setPromptStop(int bin) {
        _promptStop = bin;
    }

    @Override
    public int getPromptStop() {
        return _promptStop;
    }

    @Override
    public void setDecayStart(int bin) {
        _decayStart = bin;
    }

    @Override
    public int getDecayStart() {
        return _decayStart;
    }

    @Override
    public void setDecayStop(int bin) {
        _decayStop = bin;
    }

    @Override
    public int getDecayStop() {
        return _decayStop;
    }   
}