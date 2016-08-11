package org.ironrhino.core.fs.impl;

import static org.ironrhino.core.metadata.Profiles.DEFAULT;
import static org.ironrhino.core.metadata.Profiles.DUAL;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.fs.FileStorage;
import org.ironrhino.core.spring.configuration.ServiceImplementationConditional;
import org.ironrhino.core.util.ValueThenKeyComparator;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.google.common.io.Files;

@Component("fileStorage")
@ServiceImplementationConditional(profiles = { DEFAULT, DUAL })
public class LocalFileStorage implements FileStorage {

	@Autowired
	private Logger logger;

	@Value("${fileStorage.uri:file:///${app.context}/assets/}")
	protected String uri;

	@Value("${fileStorage.baseUrl:}")
	protected String baseUrl;

	private File directory;

	public void setUri(String uri) {
		this.uri = uri;
	}

	@PostConstruct
	public void afterPropertiesSet() {
		Assert.hasText(uri);
		uri = uri.replace('\\', '/');
		uri = uri.replace(" ", "%20");
		try {
			this.directory = new File(new URI(uri));
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}
		if (this.directory.isFile())
			throw new IllegalStateException(directory + " is not directory");
		if (!this.directory.exists())
			if (!this.directory.mkdirs())
				logger.error("mkdirs error:" + directory.getAbsolutePath());
	}

	@Override
	public void write(InputStream is, String path) throws IOException {
		path = Files.simplifyPath(path);
		File dest = new File(directory, path);
		dest.getParentFile().mkdirs();
		FileOutputStream os = new FileOutputStream(dest);
		try {
			IOUtils.copy(is, os);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}
	}

	@Override
	public InputStream open(String path) throws IOException {
		path = Files.simplifyPath(path);
		return new FileInputStream(new File(directory, path));
	}

	@Override
	public boolean mkdir(String path) {
		path = Files.simplifyPath(path);
		return new File(directory, path).mkdirs();
	}

	@Override
	public boolean delete(String path) {
		path = Files.simplifyPath(path);
		return new File(directory, path).delete();
	}

	@Override
	public long getLastModified(String path) {
		path = Files.simplifyPath(path);
		return new File(directory, path).lastModified();
	}

	@Override
	public boolean exists(String path) {
		path = Files.simplifyPath(path);
		return new File(directory, path).exists();
	}

	@Override
	public boolean rename(String fromPath, String toPath) {
		fromPath = Files.simplifyPath(fromPath);
		toPath = Files.simplifyPath(toPath);
		String s1 = fromPath.substring(0, fromPath.lastIndexOf('/'));
		String s2 = toPath.substring(0, fromPath.lastIndexOf('/'));
		if (!s1.equals(s2))
			return false;
		return new File(directory, fromPath).renameTo(new File(directory, toPath));
	}

	@Override
	public boolean isDirectory(String path) {
		path = Files.simplifyPath(path);
		return new File(directory, path).isDirectory();
	}

	@Override
	public List<String> listFiles(String path) {
		path = Files.simplifyPath(path);
		final List<String> list = new ArrayList<>();
		new File(directory, path).listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isFile()) {
					list.add(f.getName());
				}
				return false;
			}
		});
		return list;
	}

	@Override
	public Map<String, Boolean> listFilesAndDirectory(String path) {
		path = Files.simplifyPath(path);
		final Map<String, Boolean> map = new HashMap<String, Boolean>();
		new File(directory, path).listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				map.put(f.getName(), f.isFile());
				return false;
			}
		});
		List<Map.Entry<String, Boolean>> list = new ArrayList<Map.Entry<String, Boolean>>(map.entrySet());
		Collections.sort(list, comparator);
		Map<String, Boolean> sortedMap = new LinkedHashMap<String, Boolean>();
		for (Map.Entry<String, Boolean> entry : list)
			sortedMap.put(entry.getKey(), entry.getValue());
		return sortedMap;
	}

	@Override
	public String getFileUrl(String path) {
		path = Files.simplifyPath(path);
		if (!path.startsWith("/"))
			path = "/" + path;
		return StringUtils.isNotBlank(baseUrl) ? baseUrl + path : path;
	}

	private ValueThenKeyComparator<String, Boolean> comparator = new ValueThenKeyComparator<String, Boolean>() {
		@Override
		protected int compareValue(Boolean a, Boolean b) {
			return b.compareTo(a);
		}
	};
}
