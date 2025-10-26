// notification.js
class Notification {
    constructor() {
        this.container = document.createElement('div');
        this.container.className = 'notification-container position-fixed top-0 end-0 p-3';
        this.container.style.zIndex = '1050';
        document.body.appendChild(this.container);
    }

    show(message, type = 'success', duration = 3000) {
        const notification = document.createElement('div');
        notification.className = `notification alert alert-${type} d-flex align-items-center mb-2`;
        notification.style.opacity = '0';

        // Tạo SVG progress circle
        const radius = 8;
        const circumference = 2 * Math.PI * radius;

        notification.innerHTML = `
            <svg class="notification-progress" viewBox="0 0 20 20">
                <circle class="bg-circle" cx="10" cy="10" r="${radius}"></circle>
                <circle class="progress-circle" cx="10" cy="10" r="${radius}" 
                        stroke-dasharray="${circumference}" 
                        stroke-dashoffset="0"></circle>
            </svg>
            <span>${message}</span>
        `;

        this.container.appendChild(notification);

        const progressCircle = notification.querySelector('.progress-circle');

        // Animation fade in
        let opacity = 0;
        const fadeIn = setInterval(() => {
            if (opacity < 1) {
                opacity += 0.1;
                notification.style.opacity = opacity;
            } else {
                clearInterval(fadeIn);
            }
        }, 30);

        // Progress circle countdown
        const startTime = Date.now();
        const progressInterval = setInterval(() => {
            const elapsed = Date.now() - startTime;
            const remaining = Math.max(0, duration - elapsed);
            const progress = remaining / duration;

            // Cập nhật stroke-dashoffset để tạo hiệu ứng đếm ngược
            const offset = circumference * (1 - progress);
            progressCircle.style.strokeDashoffset = offset;

            if (remaining <= 0) {
                clearInterval(progressInterval);
            }
        }, 50);

        // Tắt sau duration
        setTimeout(() => {
            clearInterval(progressInterval);
            let opacity = 1;
            const fadeOut = setInterval(() => {
                if (opacity > 0) {
                    opacity -= 0.1;
                    notification.style.opacity = opacity;
                } else {
                    clearInterval(fadeOut);
                    if (this.container.contains(notification)) {
                        this.container.removeChild(notification);
                    }
                }
            }, 30);
        }, duration);

        return notification;
    }

    showSuccess(message, duration = 3000) {
        this.show(message, 'success', duration);
    }

    showError(message, duration = 3000) {
        this.show(message, 'danger', duration);
    }

    showInfo(message, duration = 3000) {
        this.show(message, 'info', duration);
    }

    showWarning(message, duration = 3000) {
        this.show(message, 'warning', duration);
    }
}

// Khởi tạo và sử dụng toàn cục
const notification = new Notification();

// Export để dùng trong các file khác nếu cần (dùng module)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = notification;
} else {
    window.notification = notification;
}