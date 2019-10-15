<?php

namespace App\Model;

use App\Infrastructure\Persistence\HTTP\CryptobankClient\ClientInterface as CryptoBankInterface;
use Illuminate\Contracts\Auth\Authenticatable;

/**
 * Class User
 * @package App\Model
 */
class User implements Authenticatable
{
    const GROUP_NEW = 0;
    const GROUP_READY_FOR_APPROVE = 1;
    const GROUP_APPROVED = 2;
    const GROUP_DISABLED = 3;
    const GROUP_CONFIRMED = 4;
    const GROUP_ADMIN_ADMIN = 10;
    const GROUP_ADMIN_SUPPORT = 11;


    private static $GROUPS
        = [
            self::GROUP_NEW               => 'new',
            self::GROUP_CONFIRMED         => 'confirmed',
            self::GROUP_READY_FOR_APPROVE => 'ready for approval',
            self::GROUP_APPROVED          => 'approved',
            self::GROUP_DISABLED          => 'disabled',
        ];

    private static $GROUPS_ADMIN
        = [
            self::GROUP_ADMIN_ADMIN   => 'admin',
            self::GROUP_ADMIN_SUPPORT => 'support'
        ];

    private $client;
    protected $rememberTokenName = 'remember_token';
    private $email;
    private $token;
    private $password;
    private $group;
    public $google2fa_secret;


    public static function getGroups()
    {
        return self::$GROUPS;
    }

    public static function getAdminGroups()
    {
        return self::$GROUPS_ADMIN;
    }


    public function __construct(CryptoBankInterface $client)
    {
        $this->client = $client;
    }


    public function getAuthIdentifierName()
    {
        return 'email';
    }

    public function getAuthIdentifier()
    {
        return $this->token;
    }

    public function getAuthPassword()
    {
        return $this->password;
    }

    public function getRememberToken()
    {
        if(!empty($this->getRememberTokenName())) {
            return $this->{$this->getRememberTokenName()};
        }
    }

    public function getRememberTokenName()
    {
        return $this->rememberTokenName;
    }

    public function fetchUserByCredentials(array $credentials)
    {
        $token = $this->client->authorize($credentials['email'], $credentials['password']);

        if($token) {
            $this->fetchByToken($token);
        }

        return $this;
    }

    public function fetchByToken($identifier)
    {
        $this->client->setAuthToken($identifier);
        $data = $this->client->account();

        if($data) {
            $this->email = $data['email'];
            $this->token = $identifier;
            $this->group = $data['group'];

            return $this;
        }

        return false;

    }

    /**
     * @return mixed
     */
    public function getEmail()
    {
        return $this->email;
    }

    /**
     * @param mixed $email
     */
    public function setEmail($email)
    {
        $this->email = $email;
    }

    public function isAdmin()
    {
        return $this->group == self::GROUP_ADMIN_ADMIN;
    }

    public function isSupport()
    {
        return $this->group == self::GROUP_ADMIN_SUPPORT;
    }

    public function destroyToken($identifier)
    {
        $this->client->setAuthToken($identifier);
        $this->client->logout();
    }

}