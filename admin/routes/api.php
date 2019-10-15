<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

//..........
Route::middleware(['auth', '2fa', 'group.admin'])->group(function () {
    //Get all transactions
    Route::get('/transactions', 'TransactionController@index');
    //Get concrete transaction by id
    Route::get('/transactions/{id}', 'TransactionController@show');

    //Get all settings
    Route::get('/settings', 'Settings\SettingsController@index');
    //Get interest settings
    Route::get('/settings/interest', 'Settings\InterestController@index');
    //Save new interest setting
    Route::post('/settings/interest', 'Settings\InterestController@create');
    //Update existing interest by id
    Route::put('/settings/interest/{id}', 'Settings\InterestController@update');
    //Delete existing interest by id
    Route::delete('/settings/interest/{id}', 'Settings\InterestController@delete');
    //Get referral settings
    Route::get('/settings/referral', 'Settings\ReferralController@index');
    //Save referral settings
    Route::post('/settings/referral', 'Settings\ReferralController@update');
    //Get withdraw settings
    Route::get('/settings/withdraw', 'Settings\WithdrawController@index');
    //Save withdraw settings
    Route::post('/settings/withdraw', 'Settings\WithdrawController@update');
    //Get tier settings
    Route::get('/settings/tier', 'Settings\TierController@index');
    //Save tier settings
    Route::post('/settings/tier', 'Settings\TierController@create');
    //Update tier settings
    Route::put('/settings/tier/{id}', 'Settings\TierController@update');
    //Delete tier settings
    Route::delete('/settings/tier/{id}', 'Settings\TierController@delete');
    //Get email settings
    Route::get('/settings/email', 'Settings\EmailController@index');
    //Save email settings
    Route::post('/settings/email', 'Settings\EmailController@update');
    //Get reports
    Route::get('/reports', 'ReportController@index');
    //Get monitoring
    Route::get('/monitoring', 'MonitoringController@index');

    //Get admin user
    Route::get('/adminUsers', 'AdminUserController@index');
    // Show create admin user form
    Route::get('/adminUsers/new', 'AdminUserController@new');
    // Change group
    Route::put('/adminUsers/{userId}/changeGroup', 'User\UserController@changeGroup');
    // Create admin user
    Route::post('/adminUsers/new', 'AdminUserController@new');
    //Get concrete admin user by id
    Route::get('/adminUsers/{id}', 'AdminUserController@show');
    // Update admin user
    Route::put('/adminUsers/{id}', 'AdminUserController@update');
    //Reset password
    Route::post('/adminUsers/{id}/resetPassword', 'User\UserController@resetPassword');

    Route::get('/admin', 'AdminController@index');
    Route::post('/ajaxTest', 'ajaxTestController@check');

    //Get message page
    Route::get('/message', 'MessageController@index');

    //Create message
    Route::post('/message', 'MessageController@send');

    //Search address
    Route::get('/message/search/{email}', 'MessageController@searchAddress');

    //Get reports page
    Route::get('/reports', 'ReportsController@index');

    Route::get('/dashboard', 'DashboardController@index');

    Route::post('/dashboard/withdraw', 'DashboardController@withdraw');
});

// user
Route::get('/users', 'User\UserController@index');
Route::get('/users/{id}', 'User\UserController@show');

Route::get('/users/{userId}/general', 'User\UserController@general');
Route::post('/users/{userId}/general', 'User\UserController@update');

Route::post('/users/{userId}/resetPassword', 'User\UserController@resetPassword');
Route::put('/users/{userId}/changeGroup', 'User\UserController@changeGroup');

Route::get('/users/{userId}/show', 'User\UserController@general');

//Get all docs
Route::get('/users/{userId}/docs', 'DocController@index');
//Get concrete doc by id
Route::get('/users/{userId}/docs/{docId}', 'DocController@show');
//Approve doc
Route::post('/users/{userId}/docs/{docId}/approve', 'DocController@approve');
//Reject doc
Route::delete('/users/{userId}/docs/{docId}/reject', 'DocController@reject');

//Get all wallets
Route::get('/users/{userId}/wallets', 'User\WalletController@index');
//Get concrete wallet by id
Route::get('/users/{userId}/wallets/{walletId}', 'User\WalletController@show');

//Get all bills
Route::get('/users/{userId}/bills', 'User\BillController@index');

//Get all requests
Route::get('/users/requests', 'RequestController@index');
//Get all requests by user id
Route::get('/users/{userId}/requests', 'RequestController@showByUser');
Route::get('/users/{userId}/requests/{requestId}', 'RequestController@show');
Route::post('/users/{userId}/requests/{requestId}', 'RequestController@update');
Route::delete('/users/{userId}/requests', 'RequestController@delete');

Route::get('/users/{userId}/history/details', 'User\HistoryController@details');
Route::get('/users/{userId}/history/login', 'User\HistoryController@login');

Route::get('/users/{userId}/referrals', 'User\ReferralController@index');
Route::get('/users/{userId}/referrals/{referralId}', 'User\ReferralController@show');

Route::get('/users/{userId}/transactions', 'User\UserController@transactions');

Route::get('/users/{userId}/message', 'User\MessageController@index');
Route::post('/users/{userId}/message', 'User\MessageController@send');