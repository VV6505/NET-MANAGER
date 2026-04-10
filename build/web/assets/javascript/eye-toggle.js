document.addEventListener('DOMContentLoaded', function() {
    const passwordFields = document.querySelectorAll('.password-field');
    passwordFields.forEach(function(field) {
        const eyeIcon = field.parentElement.querySelector('.toggle-password');
        if (!eyeIcon) return;

        // Prevent browser's default password toggle from showing
        field.setAttribute('autocomplete', 'new-password');
        
        // Handle our custom eye icon click
        eyeIcon.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            if (field.type === 'password') {
                field.type = 'text';
                eyeIcon.className = 'fa fa-eye-slash toggle-password';
            } else {
                field.type = 'password';
                eyeIcon.className = 'fa fa-eye toggle-password';
            }
        });

        // Handle input events to ensure browser's default icon doesn't interfere
        field.addEventListener('input', function() {
            if (this.value.length > 0) {
                eyeIcon.style.display = 'block';
            } else {
                eyeIcon.style.display = 'none';
            }
        });
    });
});