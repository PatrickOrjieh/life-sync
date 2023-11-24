import unittest
import app
import json

class FlaskTestCase(unittest.TestCase):

    # Set up a test client before each test
    def setUp(self):
        self.app = app.app.test_client()
        self.app.testing = True
    
    def test_registration_success(self):
        # Mock a request to the registration endpoint that should succeed
        response = self.app.post('/register', data=json.dumps({
            'name': 'test',
            'email': 'test@gmail.com',
            'password': '123qwe!"£QWE',
            'model': 'ModelX'
        }), content_type='application/json')
        
        self.assertEqual(response.status_code, 201)
        self.assertIn('Registration successful', response.data.decode())

    def test_registration_failure_missing_fields(self):
        # Mock a request with missing email field (should fail or any other missing field)
        response = self.app.post('/register', data=json.dumps({
            'name': 'test',
            'password': '123qwe!"£QWE',
            'model': 'ModelX'
        }), content_type='application/json')
        
        self.assertEqual(response.status_code, 400)
        self.assertIn('Missing parameters', response.data.decode())

    def test_registration_failure_wristband_not_found(self):
        # Mock a request with an invalid wristband model
        response = self.app.post('/register', data=json.dumps({
            'name': 'test',
            'email': 'test@gmail.com',
            'password': '123qwe!"£QWE',
            'model': 'NoModel'
        }), content_type='application/json')
        
        self.assertEqual(response.status_code, 404)
        self.assertIn('Wristband model not found', response.data.decode())

    def test_registration_failure_email_exists(self):
        # Mock a request with an email that already exists
        response = self.app.post('/register', data=json.dumps({
            'name': 'test',
            'email': 'test@gmail.com',
            'password': '123qwe!"£QWE',
            'model': 'ModelX'
        }), content_type='application/json')
        
        self.assertEqual(response.status_code, 409)
        self.assertIn('Email already exists', response.data.decode())

    def test_registration_failure_passwords_do_not_match(self):
        # Mock a request with passwords that do not match
        response = self.app.post('/register', data=json.dumps({
            'name': 'test',
            'email': 'test@gmail.com',
            'password': '123qwe!"£QWE',
            'confirmPassword': 'testpassword2',
            'model': 'ModelX'
        }), content_type='application/json')

        self.assertEqual(response.status_code, 400)
        self.assertIn('Passwords do not match', response.data.decode())

    def test_registration_failure_invalid_email(self):
        # Mock a request with an invalid email
        response = self.app.post('/register', data=json.dumps({
            'name': 'test',
            'email': 'test',
            'password': '123qwe!"£QWE',
            'confirmPassword': '123qwe!"£QWE',
            'model': 'ModelX'
        }), content_type='application/json')

        self.assertEqual(response.status_code, 400)
        self.assertIn('Invalid email', response.data.decode())

    def test_registration_failure_invalid_password(self):
        # Mock a request with an invalid password
        response = self.app.post('/register', data=json.dumps({
            'name': 'test',
            'email': 'test@gmail.com',
            'password': 'test',
            'confirmPassword': 'test',
            'model': 'ModelX'
        }), content_type='application/json')

        self.assertEqual(response.status_code, 400)
        self.assertIn('Password must be at least 8 characters long', response.data.decode())


if __name__ == '__main__':
    unittest.main()
