package ru.gbax.xmlpe.testrecord.utils;

/**
 * Модель для натурального ключа TestResord
 *
 * Created by GBAX on 27.07.2015.
 */
public class TestRecordKeyModel {

    private String depCode;
    private String depJob;

    public TestRecordKeyModel(String depCode, String depJob) {
        this.depCode = depCode;
        this.depJob = depJob;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getDepJob() {
        return depJob;
    }

    public void setDepJob(String depJob) {
        this.depJob = depJob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TestRecordKeyModel that = (TestRecordKeyModel) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(depCode, that.depCode)
                .append(depJob, that.depJob)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                .append(depCode)
                .append(depJob)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "TestRecordKeyModel{" +
                "depCode='" + depCode + '\'' +
                ", depJob='" + depJob + '\'' +
                '}';
    }
}
