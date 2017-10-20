package com.feiniu.member.dto;

import java.util.List;


public class MessageInfoAll {

	private Header header;
	private Body body;
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}



        public Body getBody() {
            return body;
        }
    
        public void setBody(Body body) {
            this.body = body;
        }



    public class Header{
		
		private String key;
		private String serialNumber;
		private String sign;
		private String timestamp;
		private String interfaceVersion;
		
		public Header(){};
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getSerialNumber() {
			return serialNumber;
		}
		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}
		public String getSign() {
			return sign;
		}
		public void setSign(String sign) {
			this.sign = sign;
		}
		public String getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}
		public String getInterfaceVersion() {
			return interfaceVersion;
		}
		public void setInterfaceVersion(String interfaceVersion) {
			this.interfaceVersion = interfaceVersion;
		}

        @Override
        public String toString() {
            return "Header [key=" + key + ", serialNumber=" + serialNumber + ", sign=" + sign
                    + ", timestamp=" + timestamp + ", interfaceVersion=" + interfaceVersion + "]";
        }
		
		
		
		
	}
	
	public class Body{
		
		private String userID;
		private String rmc;
		private List<String> messageTypes;
		private String readChannel;
		private String pageSize;
		private String pageIndex;
                private String messageStatus;
                private String beginTime;
                private String endTime;
                private String firstMsgType;
        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getRmc() {
            return rmc;
        }

        public void setRmc(String rmc) {
            this.rmc = rmc;
        }

        public List<String> getMessageTypes() {
            return messageTypes;
        }

        public void setMessageTypes(List<String> messageTypes) {
            this.messageTypes = messageTypes;
        }

        public String getReadChannel() {
            return readChannel;
        }

        public void setReadChannel(String readChannel) {
            this.readChannel = readChannel;
        }

        public String getPageSize() {
            return pageSize;
        }

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }

        public String getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(String pageIndex) {
            this.pageIndex = pageIndex;
        }

        public String getMessageStatus() {
            return messageStatus;
        }

        public void setMessageStatus(String messageStatus) {
            this.messageStatus = messageStatus;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getFirstMsgType() {
            return firstMsgType;
        }

        public void setFirstMsgType(String firstMsgType) {
            this.firstMsgType = firstMsgType;
        }

        @Override
        public String toString() {
            return "Body [userID=" + userID + ", rmc=" + rmc + ", messageTypes=" + messageTypes
                    + ", readChannel=" + readChannel + ", pageSize=" + pageSize + ", pageIndex=" + pageIndex
                    + ", messageStatus=" + messageStatus + ", beginTime=" + beginTime + ", endTime="
                    + endTime + ", firstMsgType=" + firstMsgType + "]";
        }

     

        
	}
	

    @Override
    public String toString() {
        return "MessageInfo [header=" + header + ", body=" + body + "]";
    }


	
}
