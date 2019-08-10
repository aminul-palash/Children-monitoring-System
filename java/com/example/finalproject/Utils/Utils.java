package com.example.finalproject.Utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import com.example.finalproject.Models.ClusterData;
import com.example.finalproject.Models.ContactListModel;
import com.example.finalproject.Models.HistoryModel;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class Utils {
	
	//Email Validation pattern
	public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
	//Fragments Tags
	public static final String Login_Fragment = "Login_Fragment";
	public static final String SignUp_Fragment = "SignUp_Fragment";
	public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";

	public static Cursor getAllCallLogs(ContentResolver cr) {
		// reading all data in descending order according to DATE
		String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
		Uri callUri = Uri.parse("content://call_log/calls");
		Cursor curCallLogs = cr.query(callUri, null, null, null, strOrder);
		return curCallLogs;
	}

	public static List<ContactListModel> getContactList(ContentResolver cr) {
		List<ContactListModel> ContactModelList = new ArrayList<>();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);

		if ((cur != null ? cur.getCount() : 0) > 0) {
			while (cur != null && cur.moveToNext()) {
				String id = cur.getString(
						cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(
						ContactsContract.Contacts.DISPLAY_NAME));

				if (cur.getInt(cur.getColumnIndex(
						ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[]{id}, null);
					while (pCur.moveToNext()) {
						String phoneNo = pCur.getString(pCur.getColumnIndex(
								ContactsContract.CommonDataKinds.Phone.NUMBER));
						Log.i(TAG, "Name: " + name);
						Log.i(TAG, "Phone Number: " + phoneNo);
						ContactListModel obj = new ContactListModel(name,phoneNo);
						ContactModelList.add(obj);
					}
					pCur.close();
				}
			}
		}
		if(cur!=null){
			cur.close();
		}
		return ContactModelList;
	}

	public static List<HistoryModel> getAllHistoryData(ContentResolver cr){
		List<HistoryModel> historyModelList = new ArrayList<>();
		Map<String, HistoryModel> object=new LinkedHashMap<>();
		String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
		final Uri BOOKMARKS_URI = Uri.parse("content://com.android.chrome.browser/bookmarks");
		Cursor mCur = cr.query(BOOKMARKS_URI, null, null,   null,strOrder);
		if (mCur.moveToFirst() && mCur.getCount() > 0) {
			while (mCur.isAfterLast() == false ) {
				String url = mCur.getString(mCur .getColumnIndex("url"));
				///System.out.println(url);
				String host = getHostName(url);
				object.put(host, new HistoryModel(host,url));
				mCur .moveToNext();
			}
		}

		List<String> alKeys = new ArrayList<String>(object.keySet());

		int ids=1;
		for(String strKey : alKeys){

			String domain = object.get(strKey).getDomain();
			String urls = object.get(strKey).getUrl();
			historyModelList.add(new HistoryModel(domain,urls));
			ids++;
		}
		return historyModelList;
	}

	private static String getHostName(String urlInput) {
		urlInput = urlInput.toLowerCase();
		String hostName = urlInput;
		if (!urlInput.equals("")) {
			if (urlInput.startsWith("http") || urlInput.startsWith("https")) {
				try {
					URL netUrl = new URL(urlInput);
					String host = netUrl.getHost();
					if (host.startsWith("www")) {
						hostName = host.substring("www".length() + 1);
					} else {
						hostName = host;
					}
				} catch (MalformedURLException e) {
					hostName = urlInput;
				}
			} else if (urlInput.startsWith("www")) {
				hostName = urlInput.substring("www".length() + 1);
			}
			return hostName;
		} else {
			return "";
		}
	}
	public static List<ClusterData>  PerformMlOperation(double[][] points,int records){
		ArrayList<Integer>[] oldClusters,oldClusters1,newClusters,newClusters1;
		double[][] means;
		int clusters=3,maxIterations=200;

		sortPointsByX(points, records);
		means = new double[clusters][2];
		for (int i = 0; i < means.length; i++) {
			means[i][0] = points[(int) (Math.floor((records * 1.0 / clusters) / 2) + i * records / clusters)][0];
			means[i][1] = points[(int) (Math.floor((records * 1.0 / clusters) / 2) + i * records / clusters)][1];
		}

		// Create skeletons for clusters
		oldClusters = new ArrayList[clusters];
		newClusters = new ArrayList[clusters];

		for (int i = 0; i < clusters; i++) {
			oldClusters[i] = new ArrayList<Integer>();
			newClusters[i] = new ArrayList<Integer>();
		}

		formClusters(oldClusters, means, points);
		int iterations = 0;

		// Showtime
		while (true) {

			updateMeans(oldClusters, means, points);
			formClusters(newClusters, means, points);

			iterations++;
			if (iterations > maxIterations || checkEquality(oldClusters, newClusters))
				break;
			else
				resetClusters(oldClusters, newClusters);
		}

		System.out.println("\nThe final clusters are:");
		displayOutput(oldClusters, points, means);
		System.out.println("\nIterations taken = " + iterations);

		List<ClusterData> clusterData = new ArrayList<>();
		clusterData.add(new ClusterData(Double.toString(means[0][0]),Double.toString(means[0][1]),Double.toString(oldClusters[0].size()),
				Double.toString(means[1][0]),Double.toString(means[1][1]),Double.toString(oldClusters[1].size()),
				Double.toString(means[2][0]),Double.toString(means[2][1]),Double.toString(oldClusters[2].size())  ));
		return clusterData;
	}

	static void sortPointsByX(double[][] points,int records) {
		double[] temp;

		// Bubble Sort
		for(int i=0; i<records-1; i++)
			for(int j=1; j<records; j++)
				if(points[j-1][0] > points[j][0]) {
					temp = points[j-1];
					points[j-1] = points[j];
					points[j] = temp;
				}
	}

	static void formClusters(ArrayList<Integer>[] clusterList, double[][] means, double[][] points) {
		double distance[] = new double[means.length];
		double minDistance = 999999999;
		int minIndex = 0;

		for(int i=0; i<points.length; i++) {
			minDistance = 999999999;
			for(int j=0; j<means.length; j++) {
				distance[j] = Math.sqrt(Math.pow((points[i][0] - means[j][0]), 2) + Math.pow((points[i][1] - means[j][1]), 2));
				if(distance[j] < minDistance) {
					minDistance = distance[j];
					minIndex = j;
				}
			}
			clusterList[minIndex].add(i);
		}
	}

	static void updateMeans(ArrayList<Integer>[] clusterList, double[][] means, double[][] points) {
		double totalX = 0;
		double totalY = 0;
		for(int i=0; i<clusterList.length; i++) {
			totalX = 0;
			totalY = 0;
			for(int index: clusterList[i]) {
				totalX += points[index][0];
				totalY += points[index][1];
			}
			means[i][0] = totalX/clusterList[i].size();
			means[i][1] = totalY/clusterList[i].size();
		}
	}

	static boolean checkEquality(ArrayList<Integer>[] oldClusters, ArrayList<Integer>[] newClusters) {
		for(int i=0; i<oldClusters.length; i++) {
			// Check only lengths first
			if(oldClusters[i].size() != newClusters[i].size())
				return false;
			// Check individual values if lengths are equal
			for(int j=0; j<oldClusters[i].size(); j++)
				if(oldClusters[i].get(j) != newClusters[i].get(j))
					return false;
		}

		return true;
	}

	static void resetClusters(ArrayList<Integer>[] oldClusters, ArrayList<Integer>[] newClusters) {
		for(int i=0; i<newClusters.length; i++) {
			// Copy newClusters to oldClusters
			oldClusters[i].clear();
			for(int index: newClusters[i])
				oldClusters[i].add(index);
			// Clear newClusters
			newClusters[i].clear();
		}
	}

	static void displayOutput(ArrayList<Integer>[] clusterList, double[][] points,double[][] means) {
		for(int i=0; i<clusterList.length; i++) {
			String clusterOutput = "\n\n[";
			for(int index: clusterList[i])
				clusterOutput += "(" + points[index][0] + ", " + points[index][1] + "), ";
			System.out.println(clusterOutput.substring(0, clusterOutput.length()-2) + "]");
		}

		System.out.println("The specified file was not found");
		System.out.println(clusterList[0].size());
		System.out.println(clusterList[1].size());
		System.out.println(clusterList[2].size());
		for(int i=0;i<means.length;i++) {System.out.print(means[i][0]);System.out.println(means[i][1]);}

	}

}
