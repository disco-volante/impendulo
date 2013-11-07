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

package za.ac.sun.cs.intlola.file;

import java.io.File;
import java.util.Calendar;

import za.ac.sun.cs.intlola.processing.IOUtils;

import com.google.gson.JsonObject;

/**
 * IndividualFile is an implementation of IntlolaFile for normal, non-archive
 * files.
 * 
 * @author godfried
 * 
 */
public class IndividualFile implements IntlolaFile {

	private final boolean isSrc;
	private final char mod;

	private final String path, name, pkg;

	private final long time;

	public IndividualFile(final String path, final char mod,
			final boolean isSrc) {
		this.path = path;
		this.mod = mod;
		this.isSrc = isSrc;
		time = Calendar.getInstance().getTimeInMillis();
		final String[] spd = path.split(File.separator);
		name = spd[spd.length - 1];
		pkg = IOUtils.getPackage(spd, IOUtils.NAME_SEP);
	}

	public IndividualFile(final String path, final String name,
			final String pkg, final long time, final char mod,
			final boolean isSrc) {
		this.path = path;
		this.mod = mod;
		this.isSrc = isSrc;
		this.time = time;
		this.name = name;
		this.pkg = pkg;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean sendContents() {
		return isSrc;
	}

	@Override
	public JsonObject toJSON() {
		final JsonObject ret = new JsonObject();
		if (isSrc) {
			ret.addProperty(Const.TYPE, Const.SRC);
		} else {
			ret.addProperty(Const.TYPE, Const.LAUNCH);
		}
		ret.addProperty(Const.NAME, name);
		ret.addProperty(Const.PKG, pkg);
		ret.addProperty(Const.TIME, time);
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isSrc ? 1231 : 1237);
		result = prime * result + mod;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
		result = prime * result + (int) (time ^ (time >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndividualFile other = (IndividualFile) obj;
		if (isSrc != other.isSrc)
			return false;
		if (mod != other.mod)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (pkg == null) {
			if (other.pkg != null)
				return false;
		} else if (!pkg.equals(other.pkg))
			return false;
		if (time != other.time)
			return false;
		return true;
	}

}
