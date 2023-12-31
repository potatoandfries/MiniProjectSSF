    package MiniProject2.MiniProject.model;

    import jakarta.validation.constraints.Future;
    import jakarta.validation.constraints.NotNull;

    import java.time.LocalDate;

    public class HolidayDates {

        @NotNull(message = "User ID is required")
        private String userId;

        @NotNull(message = "Start date is required")
        @Future(message = "Start date must be in the future")
        private LocalDate startDate;

        @NotNull(message = "End date is required")
        @Future(message = "End date must be in the future")
        private LocalDate endDate;

        public HolidayDates() {
        }

        public HolidayDates(@NotNull(message = "User ID is required") String userId,
                            @NotNull(message = "Start date is required") @Future(message = "Start date must be in the future") LocalDate startDate,
                            @NotNull(message = "End date is required") @Future(message = "End date must be in the future") LocalDate endDate) {
            this.userId = userId;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

    }
