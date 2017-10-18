package com.jiahua.jiahuatools.bean;
/*
 * Created by ZhiPeng Huang on 2017-10-10.
 */

import java.util.List;

public class TicketInfoJson {

    private List<TicketBean> Ticket;

    public List<TicketBean> getTicket() {
        return Ticket;
    }

    public void setTicket(List<TicketBean> Ticket) {
        this.Ticket = Ticket;
    }

    public static class TicketBean {
        /**
         * Owner : gzjh100001@jiahua.win
         * RealTillTimeNotUsed : 0
         * TypeID : 1
         * CreateTimeUnix : 1506665854
         * TicketID : 15
         * EscalationSolutionTime : 0
         * EscalationResponseTime : 0
         * StateType : open
         * Type : Unclassified
         * LockID : 2
         * Queue : test员工队列
         * SLAID :
         * EscalationUpdateTime : 0
         * Title : test开始
         * QueueID : 10
         * EscalationTime : 0
         * Age : 965709
         * StateID : 4
         * ChangeBy : 3
         * CreateBy : 3
         * ServiceID :
         * UntilTime : 0
         * Priority : 3 normal
         * Changed : 2017-10-10 18:15:16
         * CustomerUserID : null
         * ArchiveFlag : n
         * CustomerID : null
         * Responsible : david.tang@jiahua.win
         * TicketNumber : 2017092949000012
         * UnlockTimeout : 1507630516
         * PriorityID : 3
         * Lock : lock
         * GroupID : 13
         * OwnerID : 13
         * State : open
         * ResponsibleID : 2
         * Created : 2017-09-29 14:17:34
         */

        private String Owner;
        private String RealTillTimeNotUsed;
        private String TypeID;
        private String CreateTimeUnix;
        private String TicketID;
        private String EscalationSolutionTime;
        private String EscalationResponseTime;
        private String StateType;
        private String Type;
        private String LockID;
        private String Queue;
        private String SLAID;
        private String EscalationUpdateTime;
        private String Title;
        private String QueueID;
        private String EscalationTime;
        private int Age;
        private String StateID;
        private String ChangeBy;
        private String CreateBy;
        private String ServiceID;
        private int UntilTime;
        private String Priority;
        private String Changed;
        private Object CustomerUserID;
        private String ArchiveFlag;
        private Object CustomerID;
        private String Responsible;
        private String TicketNumber;
        private String UnlockTimeout;
        private String PriorityID;
        private String Lock;
        private String GroupID;
        private String OwnerID;
        private String State;
        private String ResponsibleID;
        private String Created;

        public String getOwner() {
            return Owner;
        }

        public void setOwner(String Owner) {
            this.Owner = Owner;
        }

        public String getRealTillTimeNotUsed() {
            return RealTillTimeNotUsed;
        }

        public void setRealTillTimeNotUsed(String RealTillTimeNotUsed) {
            this.RealTillTimeNotUsed = RealTillTimeNotUsed;
        }

        public String getTypeID() {
            return TypeID;
        }

        public void setTypeID(String TypeID) {
            this.TypeID = TypeID;
        }

        public String getCreateTimeUnix() {
            return CreateTimeUnix;
        }

        public void setCreateTimeUnix(String CreateTimeUnix) {
            this.CreateTimeUnix = CreateTimeUnix;
        }

        public String getTicketID() {
            return TicketID;
        }

        public void setTicketID(String TicketID) {
            this.TicketID = TicketID;
        }

        public String getEscalationSolutionTime() {
            return EscalationSolutionTime;
        }

        public void setEscalationSolutionTime(String EscalationSolutionTime) {
            this.EscalationSolutionTime = EscalationSolutionTime;
        }

        public String getEscalationResponseTime() {
            return EscalationResponseTime;
        }

        public void setEscalationResponseTime(String EscalationResponseTime) {
            this.EscalationResponseTime = EscalationResponseTime;
        }

        public String getStateType() {
            return StateType;
        }

        public void setStateType(String StateType) {
            this.StateType = StateType;
        }

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getLockID() {
            return LockID;
        }

        public void setLockID(String LockID) {
            this.LockID = LockID;
        }

        public String getQueue() {
            return Queue;
        }

        public void setQueue(String Queue) {
            this.Queue = Queue;
        }

        public String getSLAID() {
            return SLAID;
        }

        public void setSLAID(String SLAID) {
            this.SLAID = SLAID;
        }

        public String getEscalationUpdateTime() {
            return EscalationUpdateTime;
        }

        public void setEscalationUpdateTime(String EscalationUpdateTime) {
            this.EscalationUpdateTime = EscalationUpdateTime;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getQueueID() {
            return QueueID;
        }

        public void setQueueID(String QueueID) {
            this.QueueID = QueueID;
        }

        public String getEscalationTime() {
            return EscalationTime;
        }

        public void setEscalationTime(String EscalationTime) {
            this.EscalationTime = EscalationTime;
        }

        public int getAge() {
            return Age;
        }

        public void setAge(int Age) {
            this.Age = Age;
        }

        public String getStateID() {
            return StateID;
        }

        public void setStateID(String StateID) {
            this.StateID = StateID;
        }

        public String getChangeBy() {
            return ChangeBy;
        }

        public void setChangeBy(String ChangeBy) {
            this.ChangeBy = ChangeBy;
        }

        public String getCreateBy() {
            return CreateBy;
        }

        public void setCreateBy(String CreateBy) {
            this.CreateBy = CreateBy;
        }

        public String getServiceID() {
            return ServiceID;
        }

        public void setServiceID(String ServiceID) {
            this.ServiceID = ServiceID;
        }

        public int getUntilTime() {
            return UntilTime;
        }

        public void setUntilTime(int UntilTime) {
            this.UntilTime = UntilTime;
        }

        public String getPriority() {
            return Priority;
        }

        public void setPriority(String Priority) {
            this.Priority = Priority;
        }

        public String getChanged() {
            return Changed;
        }

        public void setChanged(String Changed) {
            this.Changed = Changed;
        }

        public Object getCustomerUserID() {
            return CustomerUserID;
        }

        public void setCustomerUserID(Object CustomerUserID) {
            this.CustomerUserID = CustomerUserID;
        }

        public String getArchiveFlag() {
            return ArchiveFlag;
        }

        public void setArchiveFlag(String ArchiveFlag) {
            this.ArchiveFlag = ArchiveFlag;
        }

        public Object getCustomerID() {
            return CustomerID;
        }

        public void setCustomerID(Object CustomerID) {
            this.CustomerID = CustomerID;
        }

        public String getResponsible() {
            return Responsible;
        }

        public void setResponsible(String Responsible) {
            this.Responsible = Responsible;
        }

        public String getTicketNumber() {
            return TicketNumber;
        }

        public void setTicketNumber(String TicketNumber) {
            this.TicketNumber = TicketNumber;
        }

        public String getUnlockTimeout() {
            return UnlockTimeout;
        }

        public void setUnlockTimeout(String UnlockTimeout) {
            this.UnlockTimeout = UnlockTimeout;
        }

        public String getPriorityID() {
            return PriorityID;
        }

        public void setPriorityID(String PriorityID) {
            this.PriorityID = PriorityID;
        }

        public String getLock() {
            return Lock;
        }

        public void setLock(String Lock) {
            this.Lock = Lock;
        }

        public String getGroupID() {
            return GroupID;
        }

        public void setGroupID(String GroupID) {
            this.GroupID = GroupID;
        }

        public String getOwnerID() {
            return OwnerID;
        }

        public void setOwnerID(String OwnerID) {
            this.OwnerID = OwnerID;
        }

        public String getState() {
            return State;
        }

        public void setState(String State) {
            this.State = State;
        }

        public String getResponsibleID() {
            return ResponsibleID;
        }

        public void setResponsibleID(String ResponsibleID) {
            this.ResponsibleID = ResponsibleID;
        }

        public String getCreated() {
            return Created;
        }

        public void setCreated(String Created) {
            this.Created = Created;
        }
    }
}
