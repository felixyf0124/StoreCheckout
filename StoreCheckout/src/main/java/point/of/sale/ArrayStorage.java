package point.of.sale;

import org.apache.commons.codec.digest.DigestUtils;

public class ArrayStorage extends HashStorage {

	String itemCheck = "";
	
	int readInconsistencies = 0;
	int size = 999;
	String[] array;
	
	public void testingOnlyHashPut(String barcode, String item) {
		// TODO Auto-generated method stub
		super.put(barcode, item);
	}
	
	@Override
	public void put(String barcode, String item) {
		// still write to the old HashStorage
		super.put(barcode, item);
		
		//asynch
		//shadow write
		array[Integer.parseInt(barcode)] = item;
		
		checkConsistency();
	}

	@Override
	public String barcode(String barcode) {
		//get the expected value from the old datastore
		String expected = super.barcode(barcode);
		
		//should happen asynch
		//shadow read
		String actual = array[Integer.parseInt(barcode)];
		if(!expected.equals(actual)) {
			readInconsistencies++;
			
			array[Integer.parseInt(barcode)] = expected;
			
			violation(barcode, expected, actual);
			
		}
		
		return array[Integer.parseInt(barcode)];
	}

	public int getReadInconsistencies() {
		return readInconsistencies;
	}

	public ArrayStorage() {
		array = new String[size];
	}
	
	public void forklift() {
		//copy over all the data that is in the hash
		for (String barcode : hashMap.keySet()) {
			array[Integer.parseInt(barcode)] = hashMap.get(barcode);
			
		}
	}
	
	public int checkConsistency() {
		int inconsistency = 0;
		
		for (String barcode : hashMap.keySet()) {
			String expected = hashMap.get(barcode);
			String actual = array[Integer.parseInt(barcode)];
			
			if(!expected.equals(actual)) {
				//record the inconsistency
				inconsistency++;
				//print it
				violation(barcode, expected, actual);
				
				//correct it in the new datastore
				array[Integer.parseInt(barcode)] = expected;
			}
		}
		
		return inconsistency;
		
	}
	
	private void violation(String barcode, String expected, String actual) {
		System.out.println("Consistency Violation!\n" + 
				"barcode = " + barcode +
				"\n\t expected = " + expected
				+ "\n\t actual = " + actual);
	}
	

	public void updateConsistencyCheck() {
		itemCheck = calculateConsistency();
	}
	
	private String calculateConsistency() {

		String items = "";
		
		//check the consistency of the items for sale
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				items = hashValue(items.concat(Integer.toString(i) + array[i]));
			}
		}
		
		return items;
		
	}
	
	public boolean checkArrayConsistency() {
		String actual = calculateConsistency();
		
		System.out.println("expect = " + itemCheck + " vs " + "actual = " + actual);
		
		return itemCheck.equals(actual);
	}
	
	private String hashValue(String value) {
		return DigestUtils.sha256Hex(value).toUpperCase();
	}

}
