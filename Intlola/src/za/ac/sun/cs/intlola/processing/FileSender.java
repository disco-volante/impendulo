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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.JsonObject;

public class FileSender implements Runnable {
	private final IntlolaFile file;
	private final InputStream rcv;
	private final OutputStream snd;
	private final Socket sock;

	FileSender(final IntlolaFile file, final Socket sock,
			final OutputStream snd, final InputStream rcv) {
		this.file = file;
		this.sock = sock;
		this.snd = snd;
		this.rcv = rcv;
	}

	private void closeConnection() throws IOException {
		if (sock != null) {
			sock.close();
		}
	}

	@Override
	public void run() {
		final byte[] writeBuffer = new byte[IOUtils.BUFFER_SIZE];
		FileInputStream fis = null;
		boolean ok = true;
		try {
			final JsonObject fjson = file.toJSON();
			fjson.addProperty(Const.REQ, Const.SEND);
			IOUtils.writeJson(snd, fjson);
			String received = IOUtils.read(rcv);
			if (received.startsWith(Const.OK)) {
				if (file.sendContents()) {
					try {
						fis = new FileInputStream(file.getPath());
						int count = 1;
						while ((count = fis.read(writeBuffer)) >= 0) {
							snd.write(writeBuffer, 0, count);
						}
					} catch (FileNotFoundException fe) {
						System.err.println(fe.getMessage());
					}
				}
				snd.write(Const.EOT_B);
				snd.flush();
				received = IOUtils.read(rcv);
				if (!received.startsWith(Const.OK)) {
					System.err.println("Received invalid reply: " + received);
				}
			}

		} catch (final IOException e) {
			System.err.println(e.getMessage());
			ok = false;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (final IOException e) {
				System.err.println(e.getMessage());
			}
			if (!ok) {
				try {
					closeConnection();
				} catch (final IOException e) {
					System.err.println(e.getMessage());
				}
			}

		}

	}
}
