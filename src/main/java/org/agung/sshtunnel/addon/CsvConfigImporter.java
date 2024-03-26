package org.agung.sshtunnel.addon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.programmerplanet.sshtunnel.model.Tunnel;

/**
 * 
 * @author <a href="agungm@outlook.com">Mulya Agung</a>
 */

public class CsvConfigImporter {
    public static final String COMMA_DELIMITER = ",";
	
	public final List<String> tunnelConfHeaders = new ArrayList<String>(
			Arrays.asList("localAddress", "localPort", "remoteAddress", "remotePort", "type"));
	
	public Set<Tunnel> readCsv(String csvPath) {
		HashSet<Tunnel> importedTunnels = null;
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csvPath));
			// Get header
			String line = br.readLine();
			if (line != null) {
				String[] colNamesArr = line.split(COMMA_DELIMITER);
				List<String> colNames = Arrays.asList(colNamesArr);
				//Collections.sort(colNames);
				if (colNames.equals(tunnelConfHeaders)) {
					importedTunnels = new HashSet<Tunnel>();
					while ((line = br.readLine()) != null) {
				        String[] values = line.split(COMMA_DELIMITER);
				        //records.add(Arrays.asList(values));
				        if (values.length == tunnelConfHeaders.size()) {
				        	Tunnel tunnel = new Tunnel();
				        	tunnel.setLocalAddress(values[0]);
				        	tunnel.setLocalPort(Integer.parseInt(values[1]));
				        	tunnel.setRemoteAddress(values[2]);
				        	tunnel.setRemotePort(Integer.parseInt(values[3]));
				        	tunnel.setLocal(values[4].toLowerCase().equals("local"));
				        	importedTunnels.add(tunnel);
				        }
//				        else {
//				        	System.out.println("WARNING: skipped invalid line");
//				        }
				    }
				}
//				else {
//					error = new Exception("Invalid header!\nColumn names should be " + 
//							Arrays.toString(tunnelConfHeaders.toArray()));
//				}
			}
//			else {
//				error = new Exception("Header must not be empty");
//			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return importedTunnels;
	}
}
