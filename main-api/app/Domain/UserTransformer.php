<?php

namespace App\Domain\Transformers;

use App\Domain\Model\User;
use League\Fractal\TransformerAbstract;

class UserTransformer extends TransformerAbstract
{
    protected $authorization;

    public function transform(User $user)
    {
        return [
            'address'     => $user->address,
            'group'       => $user->group,
            'birthday'    => $user->birthday,
            'email'       => $user->email,
            'firstname'   => $user->firstname,
            'lastname'    => $user->lastname,
            'phone'       => $user->phone,
            'referral'    => $user->referral,
            'zip_code'    => $user->zip_code,
            'pin_code'    => $user->pin_code,
            'files'       => $user->getFileNamesWithDates()
        ];
    }
}
