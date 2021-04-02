package com.noroff.lagalt.userhistory;


public class UserHistoryDTO {
    private long projectId;
    private long sum;

    public UserHistoryDTO(long projectId, long sum) {
        this.projectId = projectId;
        this.sum = sum;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }
}
