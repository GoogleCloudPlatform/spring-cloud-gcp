package com.google.cloud.spring.data.spanner.test.domain;

import java.util.Objects;

public class Details {
	String p1;

	String p2;

	public Details(String p1, String p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Details details = (Details) o;
		return Objects.equals(p1, details.p1) && Objects.equals(p2, details.p2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(p1, p2);
	}
}
