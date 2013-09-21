//Copyright (c) 2013, The Impendulo Authors
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification,
//are permitted provided that the following conditions are met:
//
//  Redistributions of source code must retain the above copyright notice, this
//  list of conditions and the following disclaimer.
//
//  Redistributions in binary form must reproduce the above copyright notice, this
//  list of conditions and the following disclaimer in the documentation and/or
//  other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
//ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
//ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package za.ac.sun.cs.intlola.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import za.ac.sun.cs.intlola.file.Const;

import com.google.gson.JsonObject;

/**
 * SessionEnder logs the user out of Impendulo.
 * 
 * @author godfried
 * 
 */
public class SessionEnder implements Runnable {
	private final InputStream rcv;
	private final OutputStream snd;
	private final Socket sock;

	SessionEnder(final Socket sock, final OutputStream snd,
			final InputStream rcv) {
		this.sock = sock;
		this.snd = snd;
		this.rcv = rcv;
	}

	private void closeConnection() throws IOException {
		if (snd != null) {
			snd.close();
		}
		if (rcv != null) {
			rcv.close();
		}
		if (sock != null) {
			sock.close();
		}
	}

	@Override
	public void run() {
		try {
			final JsonObject params = new JsonObject();
			params.addProperty(Const.REQ, Const.LOGOUT);
			IOUtils.writeJson(snd, params);
			final String received = IOUtils.read(rcv);
			if (!received.startsWith(Const.OK)) {
				System.err.println(received);
			}
		} catch (final IOException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				closeConnection();
			} catch (final IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
