package com.acertainmarket.utils;

public class myMap {
	private Object key1;
	private Object key2;

	@Override
	public boolean equals(Object object) {
		if (((myMap) object).key1 == null && ((myMap) object).key2 == null) {
			return true;
		}
		if (((myMap) object).key1 == null
				&& ((myMap) object).key2.equals(this.key2)) {
			return true;
		}
		if (((myMap) object).key1.equals(this.key1)
				&& ((myMap) object).key2 == null) {
			return true;
		}
		if (((myMap) object).key1.equals(this.key1)
				&& ((myMap) object).key2.equals(this.key2)) {
			return true;
		}
		return false;
	}

	public boolean compare(Object key1, Object key2) {
		if (key1.equals(key2)) {
			return true;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		int hashCode = this.key1 == null ? 0 : this.key1.hashCode();
		hashCode = hashCode + (this.key2 == null ? 0 : this.key2.hashCode());
		return hashCode;
	}

	public Object getKey1() {
		return key1;
	}

	public void setKey1(Object key1) {
		this.key1 = (Integer) key1;
	}

	public Object getKey2() {
		return key2;
	}

	public void setKey2(Object key2) {
		this.key2 = (Integer) key2;
	}

	@Override
	public String toString() {
		return "Item ID =  " + this.getKey1() + ", Buyer ID = "
				+ this.getKey2();
	}
}
